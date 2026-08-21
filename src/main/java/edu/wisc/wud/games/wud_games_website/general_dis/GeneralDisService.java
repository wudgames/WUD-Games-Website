package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisService;
import edu.wisc.wud.games.wud_games_website.equipment.Equipment;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDis;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.game_console.GameConsole;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDis;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDis;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.tag.Tag;
import edu.wisc.wud.games.wud_games_website.tag.TagRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Service("GeneralDisService")
@Transactional(rollbackFor = Exception.class)
public class GeneralDisService {

    private final GeneralDisRepository generalDisRepository;
    private final BoardGameDisRepository boardGameDisRepository;

    private final TagRepository tagRepository;
    private final ApplicationEventPublisher publisher;

    private final static Map<String, Supplier<GeneralDisDTO>> physicalDescriptionTypes = new HashMap<>();
    static {
        physicalDescriptionTypes.put("Board Game", () -> new BoardGameDisDTO());
        physicalDescriptionTypes.put("Board Game Expansion", () -> new BoardGameExpansionDisDTO());
        physicalDescriptionTypes.put("Equipment", () -> new EquipmentDisDTO());
        physicalDescriptionTypes.put("Game Console", () -> new GameConsoleDisDTO());
    }

    private final static Set<Class<? extends GeneralDisDTO>> physicalDescriptionClasses = new HashSet<>();
    static {
        physicalDescriptionClasses
                .addAll(physicalDescriptionTypes.values().stream().map(s -> s.get().getClass()).toList());
    }

    private final Map<Class<? extends GeneralDis>, Supplier<GeneralDisDTO>> entityToDTO = new HashMap<>();

    private final Map<Class<? extends GeneralDis>, Function<? extends GeneralDis, ? extends GeneralDisDTO>> entityToDTOMapper = new HashMap<>();

    // @Autowired
    // @Qualifier("BoardGameDisService")
    // private BoardGameDisService BOARD_GAME_DIS_SERVICE;
    @Autowired
    @Qualifier("BoardGameExpansionDisService")
    private BoardGameExpansionDisService BOARD_GAME_EXPANSION_DIS_SERVICE;
    @Autowired
    @Qualifier("VideoGameDisService")
    private VideoGameDisService VIDEO_GAME_DIS_SERVICE;

    public GeneralDisService(final GeneralDisRepository generalDisRepository,
            final TagRepository tagRepository, final ApplicationEventPublisher publisher,
            final BoardGameDisRepository boardGameDisRepository) {
        this.generalDisRepository = generalDisRepository;
        this.boardGameDisRepository = boardGameDisRepository;

        this.tagRepository = tagRepository;
        this.publisher = publisher;

        entityToDTO.put(GeneralDis.class, () -> new GeneralDisDTO());
        entityToDTOMapper.put(GeneralDis.class, this::mapGeneralDisToDTO);

        entityToDTO.put(GameDis.class, () -> new GameDisDTO());
        entityToDTOMapper.put(GameDis.class, new GameDisToDTOWrapper());

        entityToDTO.put(BoardGameDis.class, () -> new BoardGameDisDTO());
        entityToDTOMapper.put(BoardGameDis.class, new MapBoardGameDisToDTOWrapper());

        entityToDTO.put(BoardGameExpansionDis.class, () -> new BoardGameExpansionDisDTO());
        entityToDTOMapper.put(BoardGameExpansionDis.class, new MapBoardGameExpansionDisToDTOWrapper());

        entityToDTO.put(EquipmentDis.class, () -> new EquipmentDisDTO());
        entityToDTOMapper.put(EquipmentDis.class, new MapEquipmentDisToDTOWrapper());

        entityToDTO.put(GameConsoleDis.class, () -> new GameConsoleDisDTO());
        entityToDTOMapper.put(GameConsoleDis.class, new MapGameConsoleDisToDTOWrapper());

    }

    // TODO use this
    private void validateType(@RequestParam String description_type, HttpServletRequest request) {
        if (!typeStringMapFor(request).containsKey(description_type)) {
            throw new IllegalArgumentException("Invalid description type: " + description_type);
        }
    }

    public Map<String, Supplier<GeneralDisDTO>> typeStringMapFor(HttpServletRequest request) {
        Map<String, Supplier<GeneralDisDTO>> itemTypeOptions = new HashMap<>();
        if (request.isUserInRole("PHYSICAL_INVENTORY_MANAGER")) {
            itemTypeOptions.putAll(physicalDescriptionTypes);
        }
        // TODO add digitanal inventory options
        return itemTypeOptions;
    }

    private GeneralDisDTO getDTOFromRequest(HttpServletRequest request, Map<String, String> queryParameters) {
        // This would be the use case for create generic mappers
        System.out.println(
                "called getDTOFromRequest with request: " + request + ", and queryParameters: " + queryParameters);
        GeneralDisDTO generalDisDTO = typeStringMapFor(request).get(queryParameters.get("description_type")).get();
        System.out.println("created generalDisDTO of type " + generalDisDTO.getClass());
        generalDisDTO.setName(queryParameters.get("name"));
        generalDisDTO.setDescription(queryParameters.get("description"));
        generalDisDTO.setImageUrl(queryParameters.get("imageUrl"));
        // TODO tags
        if (generalDisDTO instanceof GameDisDTO) {
            try {
                ((GameDisDTO) generalDisDTO).setMinPlayers(Integer.valueOf(queryParameters.get("minPlayers")));
            } catch (NumberFormatException e) {
            }
            try {
                ((GameDisDTO) generalDisDTO).setMaxPlayers(Integer.valueOf(queryParameters.get("maxPlayers")));
            } catch (NumberFormatException e) {
            }
        }
        // TODO fields on other description types
        return generalDisDTO;
    }

    public void createNewDescription(HttpServletRequest request, @RequestParam Map<String, String> queryParameters) {
        // This is what run when the form is submitted
        System.out.println("ran createNewItem with queryParameters: " + queryParameters);
        GeneralDisDTO generalDisDTO = getDTOFromRequest(request, queryParameters);
        /*
         * System.out.println("ran createNewItem with module attribute of class: " +
         * generalDisDTO.getClass());
         * BoardGameDisDTO boardGameDisDTO = (BoardGameDisDTO) generalDisDTO;
         * System.out.println("module attribute values:\n" +
         * "   name: " + generalDisDTO.getName() + "\n" +
         * "   description: " + generalDisDTO.getDescription() + "\n" +
         * "   min player: " + boardGameDisDTO.getMinPlayers() + "\n"
         * );
         */
        // TODO identify the right repository to save to
        // JpaRepository<? extends GeneralDis, Long> repository =
        // descriptionsRepositories.get(generalDisDTO.getClass());
        // repository.save(generalDisDTO);

        // Make sure to check all sub-class before a parent
        if (generalDisDTO instanceof BoardGameExpansionDisDTO) {
            BOARD_GAME_EXPANSION_DIS_SERVICE.create((BoardGameExpansionDisDTO) generalDisDTO);// TODO changed to not call other service
            System.out.println("Created a board game expansion description.");
        } else if (generalDisDTO instanceof BoardGameDisDTO) {
            create((BoardGameDisDTO) generalDisDTO);
            System.out.println("Created a board game description.");
        } else if (generalDisDTO instanceof VideoGameDisDTO) {// TODO add a check for VideoGameExpansionDisDTO
            VIDEO_GAME_DIS_SERVICE.create((VideoGameDisDTO) generalDisDTO);
            System.out.println("Created a video game description.");
        } else if (generalDisDTO instanceof EquipmentDisDTO) {
            throw new UnsupportedOperationException("EquipmentDisDTO is not supported yet.");
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + generalDisDTO.getClass().getName());
        }
    }

    private void verifyAuthorizationForDescriptionType(HttpServletRequest request, final GeneralDisDTO generalDisDTO) {
        Class<? extends GeneralDisDTO> descriptionType = generalDisDTO.getClass();
        if (physicalDescriptionClasses.contains(descriptionType)) {
            if (request.isUserInRole("PHYSICAL_INVENTORY_MANAGER")) {
                return;// User is authorized
            }
            throw new InvalidParameterException("User is not authorization to edit type: " + descriptionType);
        } // else check other types here
        throw new InvalidParameterException("Failed to determine authorization to edit type: " + descriptionType);
    }

    public void updateDescription(Long id, HttpServletRequest request, final ModelAndView model) {
        // lookup description by id
        final GeneralDisDTO generalDisDTO = mapToDTO(generalDisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no description with id: " + id)));
        // validate authorization for description type
        verifyAuthorizationForDescriptionType(request, generalDisDTO);
        // set model attribute
        model.addObject("descriptionDTO", generalDisDTO);
    }

    public List<GeneralDisDTO> findAllDescriptions() {
        final List<GeneralDis> generalDescriptions = generalDisRepository.findAll(Sort.by("id"));// these are of the
                                                                                                 // leaf types
        System.out.println("generalDises: " + generalDescriptions);
        List<GeneralDisDTO> generalDisDTOs = generalDescriptions.stream().map(entity -> {
            return mapToDTO(entity);
        }).toList();
        return generalDisDTOs;
    }

    private GeneralDisDTO mapToDTO(final GeneralDis entity) {
        Class<? extends GeneralDis> type = entity.getClass();
        System.out.println("Type: " + type);
        GeneralDisDTO dto = ((Function<GeneralDis, GeneralDisDTO>) entityToDTOMapper.get(type)).apply(entity);
        return dto;
    }

    /* GeneralDisRepository Backed Methods */

    public List<GeneralDisDTO> findAll() {
        final List<GeneralDis> generalDises = generalDisRepository.findAll(Sort.by("id"));
        return generalDises.stream()
                .map(generalDis -> mapToDTO(generalDis))
                .toList();
    }

    public GeneralDisDTO get(final Long id) {
        return generalDisRepository.findById(id)
                .map(generalDis -> mapToDTO(generalDis))
                .orElseThrow(NotFoundException::new);
    }

    // This is the root type of the DTO so this can be public
    public Long create(final GeneralDisDTO generalDisDTO) {
        final GeneralDis generalDis = new GeneralDis();
        mapToEntity(generalDisDTO, generalDis);
        return generalDisRepository.save(generalDis).getId();
    }

    public void update(final Long id, final GeneralDisDTO generalDisDTO) {
        final GeneralDis generalDis = generalDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(generalDisDTO, generalDis);
        generalDisRepository.save(generalDis);
    }

    public void delete(final Long id) {
        final GeneralDis generalDis = generalDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteGeneralDis(id));
        generalDisRepository.delete(generalDis);
    }

    private GeneralDisDTO mapGeneralDisToDTO(final GeneralDis generalDis) {
        GeneralDisDTO dto = entityToDTO.get(generalDis.getClass()).get();// create dto of leaf type
        System.out.println(generalDis + " general description values are being mapped to DTO");
        dto.setId(generalDis.getId());
        dto.setName(generalDis.getName());
        dto.setDescription(generalDis.getDescription());
        dto.setImageUrl(generalDis.getImageUrl());
        dto.setTags(generalDis.getTags().stream()
                .map(tag -> tag.getId())
                .toList());
        return dto;
    }

    private GeneralDis mapToEntity(final GeneralDisDTO generalDisDTO, final GeneralDis generalDis) {
        generalDis.setName(generalDisDTO.getName());
        generalDis.setDescription(generalDisDTO.getDescription());
        generalDis.setImageUrl(generalDisDTO.getImageUrl());
        final List<Tag> tags = tagRepository.findAllById(
                generalDisDTO.getTags() == null ? List.of() : generalDisDTO.getTags());
        if (tags.size() != (generalDisDTO.getTags() == null ? 0 : generalDisDTO.getTags().size())) {
            throw new NotFoundException("one of tags not found");
        }
        generalDis.setTags(new HashSet<>(tags));
        return generalDis;
    }

    public Map<Long, Long> getGeneralDisValues() {
        return generalDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GeneralDis::getId, GeneralDis::getId));
    }

    @EventListener(BeforeDeleteTag.class)
    public void on(final BeforeDeleteTag event) {
        // remove many-to-many relations at owning side
        generalDisRepository.findAllByTagsId(event.getId())
                .forEach(generalDis -> generalDis.getTags().removeIf(tag -> tag.getId().equals(event.getId())));
    }

    /* GameDisRepository Backed Methods */

    public GameDisDTO GameDisToDTO(final GameDis gameDis) {
        GameDisDTO gameDisDTO = (GameDisDTO) GeneralDisService.this.mapGeneralDisToDTO(gameDis);
        System.out.println(gameDis + " game description values are being mapped to DTO");
        gameDisDTO.setMinPlayers(gameDis.getMinPlayers());
        gameDisDTO.setMaxPlayers(gameDis.getMaxPlayers());
        return gameDisDTO;
    }

    class GameDisToDTOWrapper implements Function<GameDis, GameDisDTO> {
        public GameDisDTO apply(final GameDis gameDis) {
            return GameDisToDTO(gameDis);
        }
    }

    private GameDisDTO mapGameDisToDTO(final GameDis gameDis) {
        GameDisDTO gameDisDTO = (GameDisDTO) mapGeneralDisToDTO(gameDis);
        System.out.println(gameDis + " game description values are being mapped to DTO");
        gameDisDTO.setMinPlayers(gameDis.getMinPlayers());
        gameDisDTO.setMaxPlayers(gameDis.getMaxPlayers());
        return gameDisDTO;
    }

    /* BoardGameDisRepository Backed Methods */

    public BoardGameDisDTO getBoardGameDis(final Long id) {
        return boardGameDisRepository.findById(id)
                .map(boardGameDis -> mapBoardGameDisToDTO(boardGameDis))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BoardGameDisDTO boardGameDisDTO) {
        final BoardGameDis boardGameDis = new BoardGameDis();
        mapToEntity(boardGameDisDTO, boardGameDis);
        return boardGameDisRepository.save(boardGameDis).getId();
    }

    private BoardGameDisDTO mapBoardGameDisToDTO(BoardGameDis boardGameDis) {
        BoardGameDisDTO boardGameDisDTO = (BoardGameDisDTO) mapGameDisToDTO(boardGameDis);
        boardGameDisDTO.setMinPlaytime(boardGameDis.getMinPlaytime());
        boardGameDisDTO.setMaxPlaytime(boardGameDis.getMaxPlaytime());
        return boardGameDisDTO;
    }

    class MapBoardGameDisToDTOWrapper implements Function<BoardGameDis, BoardGameDisDTO> {
        public BoardGameDisDTO apply(BoardGameDis boardGameDis) {
            return mapBoardGameDisToDTO(boardGameDis);
        }
    }

    /* BoardGameExpansionDisRepository Backed Methods */

    private BoardGameExpansionDisDTO mapBoardGameExpansionDisToDTO(BoardGameExpansionDis boardGameExpansionDis) {
        BoardGameExpansionDisDTO boardGameExpansionDisDTO = (BoardGameExpansionDisDTO) mapBoardGameDisToDTO(
                boardGameExpansionDis);
        boardGameExpansionDisDTO.setBaseBoardGameDis(mapBoardGameDisToDTO(boardGameExpansionDis.getBaseBoardGameDis()));
        return boardGameExpansionDisDTO;
    }

    class MapBoardGameExpansionDisToDTOWrapper implements Function<BoardGameExpansionDis, BoardGameExpansionDisDTO> {
        public BoardGameExpansionDisDTO apply(BoardGameExpansionDis boardGameExpansionDis) {
            return mapBoardGameExpansionDisToDTO(boardGameExpansionDis);
        }
    }

    /* EquipmentDisRepository Backed Methods */

    private EquipmentDisDTO mapEquipmentDisToDTO(EquipmentDis equipmentDis) {
        EquipmentDisDTO equipmentDisDTO = (EquipmentDisDTO) mapGeneralDisToDTO(equipmentDis);
        return equipmentDisDTO;
    }

    class MapEquipmentDisToDTOWrapper implements Function<EquipmentDis, EquipmentDisDTO> {
        public EquipmentDisDTO apply(EquipmentDis equipmentDis) {
            return mapEquipmentDisToDTO(equipmentDis);
        }
    }

    /* GameConsoleDisRepository Backed Methods */

    private GameConsoleDisDTO mapGameConsoleDisToDTO(GameConsoleDis gameConsoleDis) {
        GameConsoleDisDTO gameConsoleDisDTO = (GameConsoleDisDTO) mapGeneralDisToDTO(gameConsoleDis);
        return gameConsoleDisDTO;
    }

    class MapGameConsoleDisToDTOWrapper implements Function<GameConsoleDis, GameConsoleDisDTO> {
        public GameConsoleDisDTO apply(GameConsoleDis gameConsoleDis) {
            return mapGameConsoleDisToDTO(gameConsoleDis);
        }
    }
}
