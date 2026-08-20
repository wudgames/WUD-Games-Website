package edu.wisc.wud.games.wud_games_website.general_dis;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;


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

    public GeneralDisResource(final GeneralDisService generalDisService) {
        this.generalDisService = generalDisService;
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

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("manage/inventory/create")
    public ModelAndView getMethodName(Model model, @RequestParam Map<String, String> queryParameters, HttpServletRequest request) {
        Set<String> itemTypeOptions = typeOptionsFor(request).keySet();
        model.addAttribute("type_options", itemTypeOptions);
        System.out.println("set type_options to " + itemTypeOptions);
        model.addAttribute("new_item", new GeneralDisDTO());
        return new ModelAndView("manage/inventory/create");
    }

    @GetMapping("/api/manage/inventory/formoptions")
    public ModelAndView getItemForm(Model model, @RequestParam Map<String, String> queryParameters, HttpServletRequest request) {
        Map<String, Supplier<GeneralDisDTO>> itemTypeOptions = typeOptionsFor(request);
        String descriptionType = queryParameters.get("descriptionType");
        if (itemTypeOptions.containsKey(descriptionType)) {
            model.addAttribute("new_item", itemTypeOptions.get(descriptionType).get());
            model.addAttribute("description_type", descriptionType);
        } else {
            throw new IllegalArgumentException("Invalid item type: " + descriptionType);
        }
        return new ModelAndView("manage/inventory/formoptions");
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @PostMapping("/api/description/create")
    public ModelAndView createNewItem(@RequestParam Map<String, String> queryParameters) {
        System.out.println("ran createNewItem with parameters: " + queryParameters);
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

