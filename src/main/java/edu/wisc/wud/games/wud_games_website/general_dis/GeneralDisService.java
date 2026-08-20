package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisService;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.tag.Tag;
import edu.wisc.wud.games.wud_games_website.tag.TagRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service("GeneralDisService")
@Transactional(rollbackFor = Exception.class)
public class GeneralDisService {

    private final GeneralDisRepository generalDisRepository;
    private final TagRepository tagRepository;
    private final ApplicationEventPublisher publisher;

    private final static Map<String, Supplier<GeneralDisDTO>> physicalDescriptionTypes = new HashMap<String, Supplier<GeneralDisDTO>>();
    static {
        physicalDescriptionTypes.put("Board Game", () -> new BoardGameDisDTO());
        physicalDescriptionTypes.put("Board Game Expansion", () -> new BoardGameExpansionDisDTO());
        physicalDescriptionTypes.put("Equipment", () -> new EquipmentDisDTO());
        physicalDescriptionTypes.put("Game Console", () -> new GameConsoleDisDTO());
    }

    @Autowired
    @Qualifier("BoardGameDisService")
    private BoardGameDisService BOARD_GAME_DIS_SERVICE;
    @Autowired
    @Qualifier("BoardGameExpansionDisService")
    private BoardGameExpansionDisService BOARD_GAME_EXPANSION_DIS_SERVICE;
    @Autowired
    @Qualifier("VideoGameDisService")
    private VideoGameDisService VIDEO_GAME_DIS_SERVICE;

    public GeneralDisService(final GeneralDisRepository generalDisRepository,
            final TagRepository tagRepository, final ApplicationEventPublisher publisher) {
        this.generalDisRepository = generalDisRepository;
        this.tagRepository = tagRepository;
        this.publisher = publisher;
    }

    // TODO use this
    private void validateType(@RequestParam String description_type, HttpServletRequest request) {
        if (!typeOptionsFor(request).containsKey(description_type)) {
            throw new IllegalArgumentException("Invalid description type: " + description_type);
        }
    }

    public Map<String, Supplier<GeneralDisDTO>> typeOptionsFor(HttpServletRequest request) {
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
        GeneralDisDTO generalDisDTO = typeOptionsFor(request).get(queryParameters.get("description_type")).get();
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
        // JpaRepository<? extends GeneralDis, Long> repository = descriptionsRepositories.get(generalDisDTO.getClass());
        //repository.save(generalDisDTO);

        // Make sure to check all sub-class before a parent
        if (generalDisDTO instanceof BoardGameExpansionDisDTO) {
            BOARD_GAME_EXPANSION_DIS_SERVICE.create((BoardGameExpansionDisDTO) generalDisDTO);
            System.out.println("Created a board game expansion description.");
        } else if (generalDisDTO instanceof BoardGameDisDTO) {
            BOARD_GAME_DIS_SERVICE.create((BoardGameDisDTO) generalDisDTO);
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

    // Mostly Auto Generated Methods
    public List<GeneralDisDTO> findAll() {
        final List<GeneralDis> generalDises = generalDisRepository.findAll(Sort.by("id"));
        return generalDises.stream()
                .map(generalDis -> mapToDTO(generalDis, new GeneralDisDTO()))
                .toList();
    }

    public GeneralDisDTO get(final Long id) {
        return generalDisRepository.findById(id)
                .map(generalDis -> mapToDTO(generalDis, new GeneralDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    // This is the root type of the DTO so this can be public
    public Long create(final GeneralDisDTO generalDisDTO) {
        final GeneralDis generalDis = new GeneralDis();
        mapToEntity(generalDisDTO, generalDis);
        return generalDisRepository.save(generalDis).getId();
    }

    public Long create(final GeneralDis generalDis) {
        final GeneralDisDTO generalDisDTO = mapToDTO(generalDis, new GeneralDisDTO());
        return create(generalDisDTO);
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

    private GeneralDisDTO mapToDTO(final GeneralDis generalDis, final GeneralDisDTO generalDisDTO) {
        generalDisDTO.setId(generalDis.getId());
        generalDisDTO.setName(generalDis.getName());
        generalDisDTO.setDescription(generalDis.getDescription());
        generalDisDTO.setImageUrl(generalDis.getImageUrl());
        generalDisDTO.setTags(generalDis.getTags().stream()
                .map(tag -> tag.getId())
                .toList());
        return generalDisDTO;
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

}
