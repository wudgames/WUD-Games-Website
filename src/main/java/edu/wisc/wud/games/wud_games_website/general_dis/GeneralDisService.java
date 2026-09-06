package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.config.DataInitializer;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service("GeneralDisService")
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class GeneralDisService extends EntityService<GeneralDisRepository, GeneralDis, GeneralDisDTO> {

    @PersistenceContext
    private EntityManager entityManager;

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;

    public GeneralDisService(GeneralDisRepository repository, EntityMapper<GeneralDis, GeneralDisDTO> mapper,
            ApplicationEventPublisher publisher, InventoryItemRepository inventoryItemRepository,
            InventoryItemMapper inventoryItemMapper) {
        super(repository, mapper, publisher);
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemMapper = inventoryItemMapper;
    }

    public List<GeneralDisDTO> search(String query) {
        return mapper.allToDTO(repository.search(query).stream().map(dto -> (GeneralDis) dto).toList());
    }

    public ModelAndView getResultsFor(ModelAndView model, Class<? extends GeneralDisDTO> clasz, MultiValueMap<String, String> params) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QGeneralDis description = QGeneralDis.generalDis;
        
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        
        if (clasz.isInstance(GeneralDisDTO.class)) {
            String searchTerm = params.getFirst("searchterm");
            if (searchTerm != null && !searchTerm.isEmpty()) {
                booleanBuilder.and(description.name.containsIgnoreCase(searchTerm));
            }
        }

        List<GeneralDisDTO> resultsList = mapper.allToDTO(queryFactory.selectFrom(description)
                .where(booleanBuilder)
                .orderBy(description.name.asc())
                .fetch());
        // DOTO this should be able to be done as one query to the database
        List<GenDisWithAvailabilityDTO> resultsWithAvailabilityList = resultsList.stream().map(result -> {
            GenDisWithAvailabilityDTO disWithAvailabilityDTO = new GenDisWithAvailabilityDTO();
            disWithAvailabilityDTO.setGeneralDis(result);
            int totalCopies = inventoryItemRepository.findByGenDis(mapper.toEntity(result)).size();
            disWithAvailabilityDTO.setTotalCopies(totalCopies);
            disWithAvailabilityDTO.setCopiesAvailable(totalCopies - repository.getNumberCheckedOut(result.getId()));
            return disWithAvailabilityDTO;
        }).toList();

        model.addObject("resultsList", resultsWithAvailabilityList);

        return model;
    }

    public int getTotalNumberOfLegacyCheckouts(Long description_id) {
        return repository.getTotalNumberOfLegacyCheckouts(description_id, DataInitializer.TIME_FOR_LEGACY_RECORDS);
    }

    // Called when the manage description form is submitted created or updated
    public void createOrUpdateDescription(GeneralDisDTO generalDisDTO) {
        System.out.println("Starting createOrUpdateDescription with " + generalDisDTO);
        try {
            create(generalDisDTO);
            System.out.println("post-create");
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setDataForSingleDescription(Long description_id, ModelAndView model) {
        GeneralDisDTO generalDisDTO = get(description_id);
        model.addObject("description", generalDisDTO);
        GeneralDis generalDis = mapper.toEntity(generalDisDTO);
        List<InventoryItemDTO> items = inventoryItemMapper.allToDTO(inventoryItemRepository.findByGenDis(generalDis));
        model.addObject("items", items);
    }

    // What is this for?
    public Map<Long, Long> getGeneralDisValues() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GeneralDis::getId, GeneralDis::getId));
    }
    /*
    public List<GeneralDis> semanticSearch() {
        vectorStore.
    }
    */
    @EventListener(BeforeDeleteTag.class)
    public void onBeforeDeleteTag(final BeforeDeleteTag event) {
        // remove many-to-many relations at owning side
        repository.findAllByTagsId(event.getId())
                .forEach(generalDis -> generalDis.getTags().removeIf(tag -> tag.getId().equals(event.getId())));
    }

    @Override
    protected GeneralDis newEntity() {
        return new GeneralDis();
    }

    @Override
    public GeneralDisDTO newDTO() {
        return new GeneralDisDTO();
    }
}
