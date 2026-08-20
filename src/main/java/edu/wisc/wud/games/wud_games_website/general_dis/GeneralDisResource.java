package edu.wisc.wud.games.wud_games_website.general_dis;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.NotImplemented;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisRepository;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisService;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisRepository;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisService;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisRepository;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisService;

@RestController
public class GeneralDisResource {

    private final GeneralDisService generalDisService;

    private final static Map<String, Supplier<GeneralDisDTO>> physicalDescriptionTypes = new HashMap<String, Supplier<GeneralDisDTO>>();
    static {
        physicalDescriptionTypes.put("Board Game", () -> new BoardGameDisDTO());
        physicalDescriptionTypes.put("Board Game Expansion", () -> new BoardGameExpansionDisDTO());
        physicalDescriptionTypes.put("Equipment", () -> new EquipmentDisDTO());
        physicalDescriptionTypes.put("Game Console", () -> new GameConsoleDisDTO());
    }

    private final GameDisService GAME_DIS_SERVICE;
    private final BoardGameDisService BOARD_GAME_DIS_SERVICE;
    private final BoardGameExpansionDisService BOARD_GAME_EXPANSION_DIS_SERVICE;
    private final VideoGameDisService VIDEO_GAME_DIS_SERVICE;
    // private static final VideoGameExpansionDisService // Not made currently

    private final Map<Class<? extends GameDisDTO>, JpaRepository<? extends GeneralDis, Long>> descriptionsRepositories = new HashMap<>();

    //private final Map<T, Consumer<T>> saveDTOFunctions = new HashMap<>();

    public GeneralDisResource(@Qualifier("GeneralDisService") final GeneralDisService generalDisService, final GameDisService GAME_DIS_SERVICE,
            @Qualifier("BoardGameDisService")final BoardGameDisService BOARD_GAME_DIS_SERVICE,
            @Qualifier("BoardGameExpansionDisService")final BoardGameExpansionDisService BOARD_GAME_EXPANSION_DIS_SERVICE,
            @Qualifier("VideoGameDisService")final VideoGameDisService VIDEO_GAME_DIS_SERVICE) {
        this.generalDisService = generalDisService;

        this.GAME_DIS_SERVICE = GAME_DIS_SERVICE;
        this.BOARD_GAME_DIS_SERVICE = BOARD_GAME_DIS_SERVICE;
        this.BOARD_GAME_EXPANSION_DIS_SERVICE = BOARD_GAME_EXPANSION_DIS_SERVICE;
        this.VIDEO_GAME_DIS_SERVICE = VIDEO_GAME_DIS_SERVICE;
        /* 
        descriptionsRepositories.put(GameDisDTO.class, GAME_DIS_REPOSITORY);
        descriptionsRepositories.put(BoardGameDisDTO.class, BOARD_GAME_DIS_REPOSITORY);
        descriptionsRepositories.put(BoardGameExpansionDisDTO.class, BOARD_GAME_EXPANSION_DIS_REPOSITORY);
        descriptionsRepositories.put(VideoGameDisDTO.class, VIDEO_GAME_DIS_REPOSITORY);
        // VIDEO_GAME_DIS_REPOSITORY);
        */
    }

    @GetMapping("/library")
    public ModelAndView librarySearch(Model model, @RequestParam Map<String, String> queryParameters) {
        attachResults(model, queryParameters);
        return new ModelAndView("search/library");
    }

    private Map<String, Supplier<GeneralDisDTO>> typeOptionsFor(HttpServletRequest request) {
        Map<String, Supplier<GeneralDisDTO>> itemTypeOptions = new HashMap<>();
        if (request.isUserInRole("PHYSICAL_INVENTORY_MANAGER")) {
            itemTypeOptions.putAll(physicalDescriptionTypes);
        }
        // TODO add digitanal inventory options
        return itemTypeOptions;
    }

    private void validateType(@RequestParam String description_type, HttpServletRequest request) {
        if (!typeOptionsFor(request).containsKey(description_type)) {
            throw new IllegalArgumentException("Invalid description type: " + description_type);
        }
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("manage/inventory/create")
    public ModelAndView getMethodName(Model model, @RequestParam Map<String, String> queryParameters,
            HttpServletRequest request) {
        Set<String> itemTypeOptions = typeOptionsFor(request).keySet();
        model.addAttribute("type_options", itemTypeOptions);
        System.out.println("set type_options to " + itemTypeOptions);
        // model.addAttribute("new_item", new GeneralDisDTO());
        return new ModelAndView("manage/inventory/create");
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("/api/manage/inventory/formoptions")
    public ModelAndView getItemForm(Model model, @RequestParam Map<String, String> queryParameters,
            HttpServletRequest request) {
        Map<String, Supplier<GeneralDisDTO>> itemTypeOptions = typeOptionsFor(request);
        String description_type = queryParameters.get("description_type");
        System.out.println("read descriptionType from query parameters: " + description_type);
        if (itemTypeOptions.containsKey(description_type)) {
            GeneralDisDTO newDescription = itemTypeOptions.get(description_type).get();
            model.addAttribute("new_description", newDescription);
            System.out.println("set new_description to object of class " + newDescription.getClass());
            model.addAttribute("description_type", description_type);
        } else {
            throw new IllegalArgumentException("Invalid item type: " + description_type);
        }
        return new ModelAndView("manage/inventory/formoptions");
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

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @PostMapping("/api/description/create")
    public ModelAndView createNewItem(HttpServletRequest request, @RequestParam Map<String, String> queryParameters) {
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
        return new ModelAndView("redirect:/library"); // TODO go back in history instead
    }

    /* This is called to refresh the the html for displaying a search result */
    @GetMapping("/api/search")
    public ModelAndView getMethodNameM(Model model, @RequestParam Map<String, String> queryParameters) {
        attachResults(model, queryParameters);
        return new ModelAndView("search/result");
    }

    private void attachResults(Model model, Map<String, String> queryParameters) {
        System.out.println("this could do something based on: " + queryParameters.get("searchterm"));
        List<GeneralDisDTO> generalDisDTOList = generalDisService.findAll();
        model.addAttribute("generalDisDTOList", generalDisDTOList);
    }

    @GetMapping("/api/generalDiss")
    public ResponseEntity<List<GeneralDisDTO>> getAllGeneralDiss() {
        return ResponseEntity.ok(generalDisService.findAll());
    }

    @GetMapping("/api/generalDiss/{id}")
    public ResponseEntity<GeneralDisDTO> getGeneralDis(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(generalDisService.get(id));
    }

    @PostMapping("/api/generalDiss")
    public ResponseEntity<Long> createGeneralDis(
            @RequestBody @Valid final GeneralDisDTO generalDisDTO) {
        final Long createdId = generalDisService.create(generalDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/api/generalDiss/{id}")
    public ResponseEntity<Long> updateGeneralDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GeneralDisDTO generalDisDTO) {
        generalDisService.update(id, generalDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/api/generalDiss/{id}")
    public ResponseEntity<Void> deleteGeneralDis(@PathVariable(name = "id") final Long id) {
        generalDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
