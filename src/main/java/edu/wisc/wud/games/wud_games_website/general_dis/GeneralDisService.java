package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisRepository;
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
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService.BoardGameExpansionDisToDTOWrapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService.EquipmentDisToDTOWrapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService.GameConsoleDisToDTOWrapper;
import edu.wisc.wud.games.wud_games_website.tag.Tag;
import edu.wisc.wud.games.wud_games_website.tag.TagDTO;
import edu.wisc.wud.games.wud_games_website.tag.TagRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDis;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisRepository;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import static org.mockito.Mockito.description;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    private final BoardGameExpansionDisRepository boardGameExpansionDisRepository;
    final VideoGameDisRepository videoGameDisRepository;

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

    private final static Map<Class<? extends GeneralDisDTO>, String> typeToDescriptionString = new HashMap<>();
    static {
        for (Map.Entry<String, Supplier<GeneralDisDTO>> entry : physicalDescriptionTypes.entrySet()) {
            typeToDescriptionString.put(entry.getValue().get().getClass(), entry.getKey());
        }
    }

    private final Map<Class<? extends GeneralDis>, Supplier<GeneralDisDTO>> entityToDTO = new HashMap<>();
    private final Map<Class<? extends GeneralDisDTO>, Supplier<GeneralDis>> dtoToEntity = new HashMap<>();

    private final Map<Class<? extends GeneralDis>, Function<? extends GeneralDis, ? extends GeneralDisDTO>> entityToDTOMapper = new HashMap<>();
    private final Map<Class<? extends GeneralDisDTO>, Function<? extends GeneralDisDTO, ? extends GeneralDis>> DTOToEntityMapper = new HashMap<>();

    private void pairEntityAndDTO(Supplier<GeneralDis> entitySupplier, Supplier<GeneralDisDTO> dtoSupplier,
            Function<? extends GeneralDis, ? extends GeneralDisDTO> mapperEntityToDTO,
            Function<? extends GeneralDisDTO, ? extends GeneralDis> mapperDTOToEntity) {
        Class<? extends GeneralDis> entityType = entitySupplier.get().getClass();
        Class<? extends GeneralDisDTO> dtoType = dtoSupplier.get().getClass();
        entityToDTO.put(entityType, dtoSupplier);
        dtoToEntity.put(dtoType, entitySupplier);

        entityToDTOMapper.put(entityType, mapperEntityToDTO);
        DTOToEntityMapper.put(dtoType, mapperDTOToEntity);
    }

    public GeneralDisService(final GeneralDisRepository generalDisRepository,
            final TagRepository tagRepository, final ApplicationEventPublisher publisher,
            final BoardGameDisRepository boardGameDisRepository,
            final BoardGameExpansionDisRepository boardGameExpansionDisRepository,
            final VideoGameDisRepository videoGameDisRepository) {
        this.generalDisRepository = generalDisRepository;
        this.boardGameDisRepository = boardGameDisRepository;
        this.boardGameExpansionDisRepository = boardGameExpansionDisRepository;
        this.videoGameDisRepository = videoGameDisRepository;

        this.tagRepository = tagRepository;
        this.publisher = publisher;

        pairEntityAndDTO(() -> new GeneralDis(), () -> new GeneralDisDTO(),
                this::generalDisToDTO, this::generalDisToEntity);

        pairEntityAndDTO(() -> new GameDis(), () -> new GameDisDTO(),
                new GameDisToDTOWrapper(), new GameDisToEntityWrapper());
        // TODO:
        pairEntityAndDTO(() -> new BoardGameDis(), () -> new BoardGameDisDTO(),
                new BoardGameDisToDTOWrapper(), new BoardGameDisToEntityWrapper());

        pairEntityAndDTO(() -> new BoardGameExpansionDis(), () -> new BoardGameExpansionDisDTO(),
                new BoardGameExpansionDisToDTOWrapper(), new BoardGameExpansionDisToEntityWrapper());
        
        pairEntityAndDTO(() -> new EquipmentDis(), () -> new EquipmentDisDTO(),
                new EquipmentDisToDTOWrapper(), new EquipmentDisToEntityWrapper());

        pairEntityAndDTO(() -> new GameConsoleDis(), () -> new GameConsoleDisDTO(),
                new GameConsoleDisToDTOWrapper(), new GameConsoleDisToEntityWrapper());

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

    private String getTypeDescriptionFor(GeneralDisDTO generalDisDTO) {
        return typeToDescriptionString.get(generalDisDTO.getClass());
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
            createBoardGameExpansionDis((BoardGameExpansionDisDTO) generalDisDTO);
            System.out.println("Created a board game expansion description.");
        } else if (generalDisDTO instanceof BoardGameDisDTO) {
            create((BoardGameDisDTO) generalDisDTO);
            System.out.println("Created a board game description.");
        } else if (generalDisDTO instanceof VideoGameDisDTO) {// TODO add a check for VideoGameExpansionDisDTO
            throw new UnsupportedOperationException("Authentication for creating a video game description not implemented. Please contact an administrator.");
            //System.out.println("Created a video game description.");
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
        final GeneralDisDTO generalDisDTO = generalDisToDTO(generalDisRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no description with id: " + id)));
        // validate authorization for description type
        verifyAuthorizationForDescriptionType(request, generalDisDTO);
        // set model attribute
        model.addObject("description", generalDisDTO);
        System.out.println("descriptionDTO.name is: " + generalDisDTO.getName());
        String typeOption = getTypeDescriptionFor(generalDisDTO);
        model.addObject("type_options", typeOption);
        System.out.println("type_options is: " + generalDisDTO.getName());
    }

    public List<GeneralDisDTO> findAllDescriptions() {
        final List<GeneralDis> generalDescriptions = generalDisRepository.findAll(Sort.by("id"));
        // these are of the leaf types
        System.out.println("generalDises: " + generalDescriptions);
        List<GeneralDisDTO> generalDisDTOs = generalDescriptions.stream().map(entity -> {
            return mapToDTO(entity);
        }).toList();
        return generalDisDTOs;
    }

    public void setCreateOrUpdateDescriptionData(final String description_type, HttpServletRequest request, final ModelAndView model, Long description_id) {
        GeneralDisDTO generalDisDTO;
        if (description_id != null) {
            generalDisDTO = get(description_id);
        } else {
            // DOTO add other class types
            generalDisDTO = physicalDescriptionTypes.get(description_type).get();
        }
        verifyAuthorizationForDescriptionType(request, generalDisDTO);
        model.addObject("description", generalDisDTO);
        System.out.println("set description to object of class " + generalDisDTO.getClass());
        model.addObject("description_type", description_type);
    }

    private GeneralDisDTO mapToDTO(final GeneralDis entity) {
        Class<? extends GeneralDis> type = entity.getClass();
        System.out.print("Entity Type: " + type);
        GeneralDisDTO dto = ((Function<GeneralDis, GeneralDisDTO>) entityToDTOMapper.get(type)).apply(entity);
        System.out.println(", DTO Type: " + dto.getClass());
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
        generalDisToEntity(generalDisDTO);
        return generalDisRepository.save(generalDis).getId();
    }

    public void update(final Long id, final GeneralDisDTO generalDisDTO) {
        final GeneralDis generalDis = generalDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        generalDisToEntity(generalDisDTO);
        generalDisRepository.save(generalDis);
    }

    public void delete(final Long id) {
        final GeneralDis generalDis = generalDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteGeneralDis(id));
        generalDisRepository.delete(generalDis);
    }

    private GeneralDisDTO generalDisToDTO(final GeneralDis generalDis) {
        GeneralDisDTO dto = entityToDTO.get(generalDis.getClass()).get();// create dto of leaf type
        //System.out.println(generalDis + " general description values are being mapped to DTO");
        dto.setId(generalDis.getId());
        dto.setName(generalDis.getName());
        dto.setDescription(generalDis.getDescription());
        dto.setImageUrl(generalDis.getImageUrl());
        dto.setTags(generalDis.getTags().stream()
                .map(tag -> mapTagToDTO(tag))
                .toList());
        return dto;
    }

    private GeneralDis generalDisToEntity(final GeneralDisDTO generalDisDTO) {
        GeneralDis entity = dtoToEntity.get(generalDisDTO.getClass()).get();// create dto of leaf type
        entity.setId(generalDisDTO.getId());
        entity.setName(generalDisDTO.getName());
        entity.setDescription(generalDisDTO.getDescription());
        entity.setImageUrl(generalDisDTO.getImageUrl());
        entity.setTags(generalDisDTO.getTags().stream().map(
                tagDTO -> {
                    Long id = tagDTO.getId();
                    Tag tag = tagRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("failed to find a tag with id " + id));
                    if (!tagDTO.getName().equals(tag.getName())) {
                        throw new NotFoundException("The tag with the id submitted has a different name.");
                    }
                    return tag;
                }).collect(Collectors.toSet()));
        return entity;
    }

    /*
     * private GeneralDis mapToEntity(final GeneralDisDTO generalDisDTO, final
     * GeneralDis generalDis) {
     * generalDis.setName(generalDisDTO.getName());
     * generalDis.setDescription(generalDisDTO.getDescription());
     * generalDis.setImageUrl(generalDisDTO.getImageUrl());
     * final List<Tag> tags = tagRepository.findAllById(
     * generalDisDTO.getTags() == null ? List.of() : generalDisDTO.getTags());
     * if (tags.size() != (generalDisDTO.getTags() == null ? 0 :
     * generalDisDTO.getTags().size())) {
     * throw new NotFoundException("one of tags not found");
     * }
     * generalDis.setTags(new HashSet<>(tags));
     * return generalDis;
     * }
     */
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

    private GameDisDTO gameDisToDTO(final GameDis gameDis) {
        GameDisDTO gameDisDTO = (GameDisDTO) generalDisToDTO(gameDis);
        //System.out.println(gameDis + " game description values are being mapped to DTO");
        gameDisDTO.setMinPlayers(gameDis.getMinPlayers());
        gameDisDTO.setMaxPlayers(gameDis.getMaxPlayers());
        return gameDisDTO;
    }

    class GameDisToDTOWrapper implements Function<GameDis, GameDisDTO> {
        public GameDisDTO apply(final GameDis gameDis) {
            return gameDisToDTO(gameDis);
        }
    }

    private GameDis gameDisToEntity(final GameDisDTO gameDisDTO) {
        GameDis gameDis = (GameDis) generalDisToEntity(gameDisDTO);
        gameDis.setMinPlayers(gameDisDTO.getMinPlayers());
        gameDis.setMaxPlayers(gameDisDTO.getMaxPlayers());
        return gameDis;
    }

    class GameDisToEntityWrapper implements Function<GameDisDTO, GameDis> {
        public GameDis apply(final GameDisDTO gameDisTDO) {
            return gameDisToEntity(gameDisTDO);
        }
    }

    /* BoardGameDisRepository Backed Methods */

    public BoardGameDisDTO getBoardGameDis(final Long id) {
        return boardGameDisRepository.findById(id)
                .map(boardGameDis -> boardGameDisToDTO(boardGameDis))
                .orElseThrow(NotFoundException::new);
    }

    public Long createBoardGameDis(final BoardGameDisDTO boardGameDisDTO) {
        final BoardGameDis boardGameDis = boardGameDisToEntity(boardGameDisDTO);
        return boardGameDisRepository.save(boardGameDis).getId();
    }

    private BoardGameDisDTO boardGameDisToDTO(BoardGameDis boardGameDis) {
        BoardGameDisDTO boardGameDisDTO = (BoardGameDisDTO) gameDisToDTO(boardGameDis);
        boardGameDisDTO.setMinPlaytime(boardGameDis.getMinPlaytime());
        boardGameDisDTO.setMaxPlaytime(boardGameDis.getMaxPlaytime());
        return boardGameDisDTO;
    }

    class BoardGameDisToDTOWrapper implements Function<BoardGameDis, BoardGameDisDTO>  {
        public BoardGameDisDTO apply(final BoardGameDis boardGameDis) {
            return boardGameDisToDTO(boardGameDis);
        }
    }

    private BoardGameDis boardGameDisToEntity(BoardGameDisDTO boardGameDisDTO) {
        BoardGameDis boardGameDis = (BoardGameDis) gameDisToEntity(boardGameDisDTO);
        boardGameDis.setMinPlaytime(boardGameDisDTO.getMinPlaytime());
        boardGameDis.setMaxPlaytime(boardGameDisDTO.getMaxPlaytime());
        return boardGameDis;
    }

    class BoardGameDisToEntityWrapper implements Function<BoardGameDisDTO, BoardGameDis> {
        public BoardGameDis apply(BoardGameDisDTO boardGameDisDTO) {
            return boardGameDisToEntity(boardGameDisDTO);
        }
    }

    /* BoardGameExpansionDisRepository Backed Methods */

    public Long createBoardGameExpansionDis(final BoardGameExpansionDisDTO boardGameExpansionDisDTO) {
        final BoardGameExpansionDis boardGameExpansionDis = boardGameExpansionDisToEntity(boardGameExpansionDisDTO);
        return boardGameExpansionDisRepository.save(boardGameExpansionDis).getId();
    }

    private BoardGameExpansionDisDTO boardGameExpansionDisToDTO(BoardGameExpansionDis boardGameExpansionDis) {
        BoardGameExpansionDisDTO boardGameExpansionDisDTO = (BoardGameExpansionDisDTO) boardGameDisToDTO(
                boardGameExpansionDis);
        boardGameExpansionDisDTO.setBaseBoardGameDis(boardGameDisToDTO(boardGameExpansionDis.getBaseBoardGameDis()));
        return boardGameExpansionDisDTO;
    }

    class BoardGameExpansionDisToDTOWrapper implements Function<BoardGameExpansionDis, BoardGameExpansionDisDTO> {
        public BoardGameExpansionDisDTO apply(BoardGameExpansionDis boardGameExpansionDis) {
            return boardGameExpansionDisToDTO(boardGameExpansionDis);
        }
    }

    private BoardGameExpansionDis boardGameExpansionDisToEntity(BoardGameExpansionDisDTO boardGameExpansionDisDTO) {
        BoardGameExpansionDis boardGameExpansionDis = (BoardGameExpansionDis) boardGameDisToEntity(
                boardGameExpansionDisDTO);
        return boardGameExpansionDis;
    }

    class BoardGameExpansionDisToEntityWrapper implements Function<BoardGameExpansionDisDTO, BoardGameExpansionDis> {
        public BoardGameExpansionDis apply(BoardGameExpansionDisDTO boardGameExpansionDisDTO) {
            return boardGameExpansionDisToEntity(boardGameExpansionDisDTO);
        }
    }

    /* EquipmentDisRepository Backed Methods */

    private EquipmentDisDTO equipmentDisToDTO(EquipmentDis equipmentDis) {
        EquipmentDisDTO equipmentDisDTO = (EquipmentDisDTO) generalDisToDTO(equipmentDis);
        return equipmentDisDTO;
    }

    class EquipmentDisToDTOWrapper implements Function<EquipmentDis, EquipmentDisDTO> {
        public EquipmentDisDTO apply(EquipmentDis equipmentDis) {
            return equipmentDisToDTO(equipmentDis);
        }
    }

    private EquipmentDis equipmentDisToEntity(EquipmentDisDTO equipmentDisDTO) {
        EquipmentDis equipmentDis = (EquipmentDis) generalDisToEntity(equipmentDisDTO);
        return equipmentDis;
    }

    class EquipmentDisToEntityWrapper implements Function<EquipmentDisDTO, EquipmentDis> {
        public EquipmentDis apply(EquipmentDisDTO equipmentDisDTO) {
            return equipmentDisToEntity(equipmentDisDTO);
        }
    }

    /* GameConsoleDisRepository Backed Methods */

    private GameConsoleDisDTO gameConsoleDisToDTO(GameConsoleDis gameConsoleDis) {
        GameConsoleDisDTO gameConsoleDisDTO = (GameConsoleDisDTO) generalDisToDTO(gameConsoleDis);
        return gameConsoleDisDTO;
    }

    class GameConsoleDisToDTOWrapper implements Function<GameConsoleDis, GameConsoleDisDTO> {
        public GameConsoleDisDTO apply(GameConsoleDis gameConsoleDis) {
            return gameConsoleDisToDTO(gameConsoleDis);
        }
    }

    private GameConsoleDis gameConsoleDisToEntity(GameConsoleDisDTO gameConsoleDisDTO) {
        GameConsoleDis gameConsoleDis = (GameConsoleDis) generalDisToEntity(gameConsoleDisDTO);
        return gameConsoleDis;
    }

    class GameConsoleDisToEntityWrapper implements Function<GameConsoleDisDTO, GameConsoleDis> {
        public GameConsoleDis apply(GameConsoleDisDTO gameConsoleDisDTO) {
            return gameConsoleDisToEntity(gameConsoleDisDTO);
        }
    }

    /* VideoGameDisRepository Backed Methods */

    public Long createVideoGameDis(final VideoGameDisDTO videoGameDisDTO) {
        final VideoGameDis videoGameDis = videoGameDisToEntity(videoGameDisDTO);
        return videoGameDisRepository.save(videoGameDis).getId();
    }
    
    private VideoGameDisDTO videoGameDisToDTO(VideoGameDis videoGameDis) {
        VideoGameDisDTO videoGameDisDTO = (VideoGameDisDTO) gameDisToDTO(videoGameDis);
        return videoGameDisDTO;
    }

    class MapVideoGameDisToDTOWrapper implements Function<VideoGameDis, VideoGameDisDTO> {
        public VideoGameDisDTO apply(VideoGameDis videoGameDis) {
            return videoGameDisToDTO(videoGameDis);
        }
    }

    private VideoGameDis videoGameDisToEntity(VideoGameDisDTO videoGameDisDTO) {
        VideoGameDis videoGameDis = (VideoGameDis) gameDisToEntity(videoGameDisDTO);
        return videoGameDis;
    }

    class VideoGameDisToEntityWrapper implements Function<VideoGameDisDTO, VideoGameDis> {
        public VideoGameDis apply(VideoGameDisDTO videoGameDisDTO) {
            return videoGameDisToEntity(videoGameDisDTO);
        }
    } 

    /* TagRepository Backed Methods */

    private TagDTO mapTagToDTO(Tag tag) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        return tagDTO;
    }

    private Tag mapTagToEntity(TagDTO tagDTO) {
        Tag tag = new Tag();
        tag.setId(tagDTO.getId());
        tag.setName(tagDTO.getName());
        return tag;
    }
}
