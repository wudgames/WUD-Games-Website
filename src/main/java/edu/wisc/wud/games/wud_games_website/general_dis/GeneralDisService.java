package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;

import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

@Service("GeneralDisService")
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class GeneralDisService extends EntityService<GeneralDisRepository, GeneralDis, GeneralDisDTO> {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;

    public GeneralDisService(GeneralDisRepository repository, EntityMapper<GeneralDis, GeneralDisDTO> mapper,
            ApplicationEventPublisher publisher, InventoryItemRepository inventoryItemRepository, InventoryItemMapper inventoryItemMapper) {
        super(repository, mapper, publisher);
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemMapper = inventoryItemMapper;
    }
    /*
    // This is used to fill in the existing data for 
    public void setCreateOrUpdateDescriptionData(final String description_type, HttpServletRequest request,
            final ModelAndView model, Long description_id) {
        GeneralDisDTO generalDisDTO;
        if (description_id != null) {
            generalDisDTO = get(description_id);
        } else {
            // DOTO add other class types
            generalDisDTO = physicalDescriptionTypes.get(description_type).get();
        }
        model.addObject("description", generalDisDTO);
        // System.out.println("set description to object of class " +
        // generalDisDTO.getClass());
        model.addObject("description_type", description_type);
        // Authorization should then be check in the resource
    }
    */
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

    @EventListener(BeforeDeleteTag.class)
    public void on(final BeforeDeleteTag event) {
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
