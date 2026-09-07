package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDisDTO;
import edu.wisc.wud.games.wud_games_website.account_dis.QAccountDis;
import edu.wisc.wud.games.wud_games_website.board_game.QBoardGame;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.QBoardGameDis;
import edu.wisc.wud.games.wud_games_website.config.DataInitializer;
import edu.wisc.wud.games.wud_games_website.console_account_dis.ConsoleAccountDisDTO;
import edu.wisc.wud.games.wud_games_website.console_account_dis.QConsoleAccountDis;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.equipment_dis.QEquipmentDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.QGameConsoleDis;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.QGameDis;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.steam_account_dis.QSteamAccountDis;
import edu.wisc.wud.games.wud_games_website.steam_account_dis.SteamAccountDisDTO;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.video_game_dis.QVideoGameDis;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashMap;
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
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service("GeneralDisService")
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class GeneralDisService extends EntityService<GeneralDisRepository, GeneralDis, GeneralDisDTO> {

    private static final Map<Class<? extends GeneralDisDTO>, EntityPathBase<? extends GeneralDis>> entityPathMap = new HashMap<>();
    static {
        entityPathMap.put(GeneralDisDTO.class, QGeneralDis.generalDis);
        entityPathMap.put(GameDisDTO.class, QGameDis.gameDis);
        entityPathMap.put(BoardGameDisDTO.class, QBoardGameDis.boardGameDis);
        entityPathMap.put(VideoGameDisDTO.class, QVideoGameDis.videoGameDis);
        entityPathMap.put(EquipmentDisDTO.class, QEquipmentDis.equipmentDis);
        entityPathMap.put(GameConsoleDisDTO.class, QGameConsoleDis.gameConsoleDis);
        entityPathMap.put(AccountDisDTO.class, QAccountDis.accountDis);
        entityPathMap.put(SteamAccountDisDTO.class, QSteamAccountDis.steamAccountDis);
        entityPathMap.put(ConsoleAccountDisDTO.class, QConsoleAccountDis.consoleAccountDis);
    }

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

    public ModelAndView getResultsFor(ModelAndView model, Class<? extends GeneralDisDTO> clasz,
            MultiValueMap<String, String> params) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        // EntityPathBase<? extends GeneralDis> description = entityPathMap.get(clasz);
        QGeneralDis description = QGeneralDis.generalDis;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (GeneralDisDTO.class.isAssignableFrom(clasz)) {
            String searchTerm = params.getFirst("searchterm");
            if (searchTerm != null && !searchTerm.isEmpty()) {
                // description.as(QGeneralDis.class)
                booleanBuilder.and(description.name.containsIgnoreCase(searchTerm));
            }
        }

        if (GameDisDTO.class.isAssignableFrom(clasz)) {
            String playCountParam = params.getFirst("playerCount");
            try {
                Integer playerCount = Integer.valueOf(playCountParam);
                if (playerCount > 0) {
                    booleanBuilder.and(description.as(QGameDis.class).minPlayers.loe(playerCount));
                    booleanBuilder.and(description.as(QGameDis.class).maxPlayers.goe(playerCount));
                }
            } catch (NumberFormatException e) {
            }
        }

        if (BoardGameDisDTO.class.isAssignableFrom(clasz)) {
            String playTimeParam = params.getFirst("playTime");
            try {
                Integer playerTime = Integer.valueOf(playTimeParam);
                if (playerTime > 0) {
                    // booleanBuilder.and(description.as(QBoardGameDis.class).minPlaytime.loe(playerTime));
                    booleanBuilder.and(description.as(QBoardGameDis.class).maxPlaytime.loe(playerTime));
                }
            } catch (NumberFormatException e) {
            }
        }

        JPAQuery<GeneralDis> query = queryFactory.selectFrom((QGeneralDis) description)
                .where(booleanBuilder);
        // .orderBy(description.name.desc())

        ComparableExpressionBase<?> sortField = null;
        // Determine field to sort by
        String sortBy = params.getFirst("sortBy");
        if (sortBy.equals("Name")) {
            sortField = description.name;
        } else if (sortBy.equals("Popularity")) {
            throw new IllegalStateException("Sorting by Popularity is not implemented");
            // sortField = description.name;
        } else if (sortBy.equals("Min Players") && GameDisDTO.class.isAssignableFrom(clasz)) {
            sortField = description.as(QGameDis.class).minPlayers;
        } else if (sortBy.equals("Max Players") && GameDisDTO.class.isAssignableFrom(clasz)) {
            sortField = description.as(QGameDis.class).maxPlayers;
        } else if (sortBy.equals("Min Playtime") && BoardGameDisDTO.class.isAssignableFrom(clasz)) {
            sortField = description.as(QBoardGameDis.class).minPlaytime;
        } else if (sortBy.equals("Max Playtime") && BoardGameDisDTO.class.isAssignableFrom(clasz)) {
            sortField = description.as(QBoardGameDis.class).maxPlaytime;
        } else {
            throw new IllegalArgumentException("Invalid sortBy parameter: " + sortBy);
        }

        // Determine sorting order
        String sortOrder = params.getFirst("sortOrder");
        if (sortOrder.equals("Ascending")) {
            query = query.orderBy(sortField.asc());
        } else if (sortOrder.equals("Descending")) {
            query = query.orderBy(sortField.desc());
        } else {
            throw new IllegalArgumentException("Invalid sortOrder parameter: " + sortOrder);
        }

        List<GeneralDisDTO> resultsList = mapper.allToDTO(query.fetch());

        // DOTO this should be able to be done as one query to the database
        List<GenDisWithAvailabilityDTO> resultsWithAvailabilityList = resultsList.stream().map(result -> {
            return getDescriptionAndAvailability(result);
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

    public GenDisWithAvailabilityDTO getDescriptionAndAvailability(GeneralDisDTO description) {
        GenDisWithAvailabilityDTO disWithAvailabilityDTO = new GenDisWithAvailabilityDTO();
        disWithAvailabilityDTO.setDescription(description);
        List<InventoryItemDTO> allItems = inventoryItemMapper
                .allToDTO(inventoryItemRepository.findByGenDis(mapper.toEntity(description)));
        disWithAvailabilityDTO.setAllItems(allItems);
        List<InventoryItemDTO> itemsCheckedOut = inventoryItemMapper
                .allToDTO(repository.getItemsCheckedOut(description.getId()));
        disWithAvailabilityDTO.setCheckedOutItems(itemsCheckedOut);
        return disWithAvailabilityDTO;
    }

    public GenDisWithAvailabilityDTO getDescriptionAndAvailability(Long id) {
        return getDescriptionAndAvailability(get(id));
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
     * public List<GeneralDis> semanticSearch() {
     * vectorStore.
     * }
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
