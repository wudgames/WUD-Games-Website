package edu.wisc.wud.games.wud_games_website.controller;

import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;

@RestController
@PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
public class InventoryManagementResource {
    private final GeneralDisService generalDisService;

    private final Map<Class<? extends GeneralDisDTO>, String> physicalDescriptionStrings = new HashMap<>();
    // Used when  create a new entity
    private final Map<Class<? extends GeneralDisDTO>, Supplier<? extends GeneralDisDTO>> dtoFactories = new HashMap<>();

    public InventoryManagementResource(@Qualifier("GeneralDisService") final GeneralDisService generalDisService) {
        this.generalDisService = generalDisService;

        physicalDescriptionStrings.put(BoardGameDisDTO.class, "Board Game");
        dtoFactories.put(BoardGameDisDTO.class, () -> new BoardGameDisDTO());

        physicalDescriptionStrings.put(BoardGameExpansionDisDTO.class, "Board Game Expansion");
        dtoFactories.put(BoardGameExpansionDisDTO.class, () -> new BoardGameExpansionDisDTO());

        physicalDescriptionStrings.put(EquipmentDisDTO.class, "Equipment");
        dtoFactories.put(EquipmentDisDTO.class, () -> new EquipmentDisDTO());

        physicalDescriptionStrings.put(GameConsoleDisDTO.class, "Game Console");
        dtoFactories.put(GameConsoleDisDTO.class, () -> new GameConsoleDisDTO());
    }
    

    private Set<String> getTypeAuthorizedOptionsFor(HttpServletRequest request) {
        Set<String> authorizedTypeStrings = new HashSet<>();
        if (request.isUserInRole("PHYSICAL_INVENTORY_MANAGER")) {
            authorizedTypeStrings.addAll(physicalDescriptionStrings.values());
        }
        // TODO: Add more authorized types as needed
        return authorizedTypeStrings;
    }

    private void verifyAuthorizationForDescriptionType(HttpServletRequest request, Class descriptionType) {
        if (physicalDescriptionStrings.keySet().contains(descriptionType)) {
            if (request.isUserInRole("PHYSICAL_INVENTORY_MANAGER")) {
                return;// User is authorized
            }
            throw new InvalidParameterException("User is not authorization to edit type: " + descriptionType);
        } // else check other types here
        throw new InvalidParameterException("Failed to determine authorization to edit type: " + descriptionType);
    }

    @GetMapping("manage/inventory/create")
    public ModelAndView getMethodName(@RequestParam Map<String, String> queryParameters,
            HttpServletRequest request) {
        ModelAndView model = new ModelAndView("manage/descriptions/page");
        Set<String> itemTypeOptions = getTypeAuthorizedOptionsFor(request);
        model.addObject("type_options", itemTypeOptions);
        System.out.println("set type_options to " + itemTypeOptions);
        // model.addAttribute("new_item", new GeneralDisDTO());
        return model;
    }

    @GetMapping("manage/inventory/update/{id}")
    public ModelAndView getMethodName(@PathVariable Long id, HttpServletRequest request) {
        ModelAndView model = new ModelAndView("search/singleDescription");
        System.out.println("Loading type for description with id: " + id);
        GeneralDisDTO generalDisDTO = generalDisService.get(id);
        System.out.println("found " + generalDisDTO);
        verifyAuthorizationForDescriptionType(request, generalDisDTO.getClass());
        model.addObject("show_editor", true);
        model.addObject("description_type", physicalDescriptionStrings.get(generalDisDTO.getClass()));
        model.addObject("description", generalDisDTO);// This is used to pass the id along to the form options
        Set<String> type_option = new HashSet<>();
        type_option.add(physicalDescriptionStrings.get(generalDisDTO.getClass()));
        model.addObject("type_options", type_option);
        return model;
    }

    private GeneralDisDTO getEmptyDTOFor(String description_type) {
        GeneralDisDTO descriptionDTO = null;
        for (Entry<Class<? extends GeneralDisDTO>, String> entry : physicalDescriptionStrings.entrySet()) {
            if (description_type.equals(entry.getValue())) {
                descriptionDTO = dtoFactories.get(entry.getKey()).get();
                break;
            }
        }
        if (description_type == null) {
            throw new IllegalArgumentException("Invalid description type: " + description_type);
        }
        return descriptionDTO;
    }

    @GetMapping("/api/manage/descriptions/formoptions")
    public ModelAndView getItemForm(@RequestParam(required = false) Long id, @RequestParam String description_type,
            HttpServletRequest request) {
        GeneralDisDTO generalDisDTO;
        System.out.println("Loading formoptions for description with id: " + id);
        if (id != null) {
            generalDisDTO = generalDisService.get(id);
        } else {
            generalDisDTO = getEmptyDTOFor(description_type);
        }
        System.out.println("found " + generalDisDTO);
        verifyAuthorizationForDescriptionType(request, generalDisDTO.getClass());

        // System.out.println("read queryParameters: " + queryParameters);
        ModelAndView model = new ModelAndView("manage/descriptions/formfields");
        model.addObject("description", generalDisDTO);
        model.addObject("description_type", description_type);
        return model;
    }

    private GeneralDisDTO parseToGeneralDisDTO(Map<String, String> queryParameters) {
        GeneralDisDTO descriptionDTO = getEmptyDTOFor(queryParameters.get("description_type"));
        System.out.println(descriptionDTO);
        if (descriptionDTO instanceof GeneralDisDTO) {
            try {
                descriptionDTO.setId(Long.valueOf(queryParameters.get("id")));
            } catch (NumberFormatException e) {
            }
            descriptionDTO.setName(queryParameters.get("name"));
            descriptionDTO.setDescription(queryParameters.get("description"));
            descriptionDTO.setImageUrl(queryParameters.get("imageUrl"));
            descriptionDTO.setTags(new ArrayList<>());
            // TODO tags
        }

        if (descriptionDTO instanceof GameDisDTO) {
            GameDisDTO gamsDisDTO = (GameDisDTO) descriptionDTO;
            try {
                gamsDisDTO.setMinPlayers(Integer.valueOf(queryParameters.get("minPlayers")));
            } catch (NumberFormatException e) {
            }
            try {
                gamsDisDTO.setMaxPlayers(Integer.valueOf(queryParameters.get("maxPlayers")));
            } catch (NumberFormatException e) {
            }

            if (gamsDisDTO instanceof BoardGameDisDTO) {
                BoardGameDisDTO boardGameDisDTO = (BoardGameDisDTO) gamsDisDTO;
                try {
                    boardGameDisDTO.setMinPlaytime(Integer.valueOf(queryParameters.get("minPlaytime")));
                } catch (NumberFormatException e) {
                }
                try {
                    boardGameDisDTO.setMaxPlaytime(Integer.valueOf(queryParameters.get("maxPlaytime")));
                } catch (NumberFormatException e) {
                }
            }
        }

        return descriptionDTO;
    }
    
    @PostMapping("/api/description/create")
    public ModelAndView createNewItem(HttpServletRequest request,
            @RequestParam Map<String, String> queryParameters) {
        // ModelAttribute is no respect polymorphic types so we need to use the
        // description_type
        GeneralDisDTO descriptionDTO = parseToGeneralDisDTO(queryParameters);
        verifyAuthorizationForDescriptionType(request, descriptionDTO.getClass());
        generalDisService.createOrUpdateDescription(descriptionDTO);
        return new ModelAndView("redirect:/library"); // TODO go back in history instead
    }
}
