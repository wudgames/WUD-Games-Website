package edu.wisc.wud.games.wud_games_website.controller;

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
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisRepository;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisService;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisRepository;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisService;

@RestController
public class GeneralDisResource {

    private final GeneralDisService generalDisService;

    private final GameDisService GAME_DIS_SERVICE;
    
    // private static final VideoGameExpansionDisService // Not made currently

    private final Map<Class<? extends GameDisDTO>, JpaRepository<? extends GeneralDis, Long>> descriptionsRepositories = new HashMap<>();

    //private final Map<T, Consumer<T>> saveDTOFunctions = new HashMap<>();

    public GeneralDisResource(@Qualifier("GeneralDisService") final GeneralDisService generalDisService, final GameDisService GAME_DIS_SERVICE) {
        this.generalDisService = generalDisService;

        this.GAME_DIS_SERVICE = GAME_DIS_SERVICE;
        
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

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("manage/inventory/create")
    public ModelAndView getMethodName(@RequestParam Map<String, String> queryParameters,
            HttpServletRequest request) {
        ModelAndView model = new ModelAndView("manage/descriptions/page");
        Set<String> itemTypeOptions = generalDisService.typeStringMapFor(request).keySet();
        model.addObject("type_options", itemTypeOptions);
        System.out.println("set type_options to " + itemTypeOptions);
        // model.addAttribute("new_item", new GeneralDisDTO());
        return model;
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("manage/inventory/update/{id}")
    public ModelAndView getMethodName(@PathVariable Long id, HttpServletRequest request) {
        ModelAndView model = new ModelAndView("manage/descriptions/page");
        generalDisService.updateDescription(id, request, model);
        return model;
    }
    

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("/api/manage/descriptions/formoptions")
    public ModelAndView getItemForm(@RequestParam Map<String, String> queryParameters,
            HttpServletRequest request) {
        //System.out.println("read queryParameters: " + queryParameters);
        ModelAndView model = new ModelAndView("manage/descriptions/formfields");
        //String description_type = queryParameters.get("description_type");
        System.out.println("read description_type from query parameters: " + description_type);
        Long description_id = Long.decode(queryParameters.get("description_id"));
        //System.out.println("read description_id from query parameters: " + description_id);
        generalDisService.setCreateOrUpdateDescriptionData(description_type, request, model, description_id);
        // generalDisService.
        return model;
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @PostMapping("/api/description/create")
    public ModelAndView createNewItem(HttpServletRequest request, @RequestParam Map<String, String> queryParameters) {
        generalDisService.createOrUpdateDescription(request, queryParameters);
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
        List<GeneralDisDTO> generalDisDTOList = generalDisService.findAllDescriptions();
        model.addAttribute("generalDisDTOList", generalDisDTOList);
    }
    /*
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
    */
}
