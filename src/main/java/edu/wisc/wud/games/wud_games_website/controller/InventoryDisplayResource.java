package edu.wisc.wud.games.wud_games_website.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.management.RuntimeErrorException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;

@RestController
public class InventoryDisplayResource {
    private final GeneralDisService generalDisService;

    // Used to pick what filter options to show and what service will handle the search
    private final Map<Class<? extends GeneralDisDTO>, Consumer<Map<String, String>>> serviceBySharedAncestor = new HashMap<>();
    private final Map<String, Class<? extends GeneralDisDTO>> classByDisplayString = new HashMap<>();

    public InventoryDisplayResource(GeneralDisService generalDisService) {
        this.generalDisService = generalDisService;

        classByDisplayString.put("Board Game", BoardGameDisDTO.class);
        classByDisplayString.put("Board Game Expansion", BoardGameExpansionDisDTO.class);
        classByDisplayString.put("Video Game", VideoGameDisDTO.class);
    }

    @GetMapping("/library")
    public ModelAndView librarySearch(@RequestParam Map<String, String> queryParameters) {
        ModelAndView model = new ModelAndView("search/library");
        model = attachResults(model, queryParameters);
        return model;
    }

    @GetMapping("/library/{id}/{vanity_name}")
    public ModelAndView getPageForDescription(@PathVariable Long id) {
        ModelAndView model = new ModelAndView("search/singleDescription");
        GeneralDisDTO generalDisDTO = generalDisService.get(id);
        model.addObject("description", generalDisDTO);
        //model.addObject("id", id);
        return model;
    }

    @GetMapping("/library/{id}")
    public ModelAndView getPageByIdOnly(@PathVariable Long id) {
        return getPageForDescription(id);
    }

    private static Set<Class<?>> getClassesBfs(Class<?> clazz) {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        Set<Class<?>> nextLevel = new LinkedHashSet<Class<?>>();
        nextLevel.add(clazz);
        do {
            classes.addAll(nextLevel);
            Set<Class<?>> thisLevel = new LinkedHashSet<Class<?>>(nextLevel);
            nextLevel.clear();
            for (Class<?> each : thisLevel) {
                Class<?> superClass = each.getSuperclass();
                if (superClass != null && superClass != Object.class) {
                    nextLevel.add(superClass);
                }
                for (Class<?> eachInt : each.getInterfaces()) {
                    nextLevel.add(eachInt);
                }
            }
        } while (!nextLevel.isEmpty());
        return classes;
    }

    private static List<Class<?>> commonSuperClass(Class<?>... classes) {
        // start off with set from first hierarchy
        Set<Class<?>> rollingIntersect = new LinkedHashSet<Class<?>>(
                getClassesBfs(classes[0]));
        // intersect with next
        for (int i = 1; i < classes.length; i++) {
            rollingIntersect.retainAll(getClassesBfs(classes[i]));
        }
        return new LinkedList<Class<?>>(rollingIntersect);
    }

    private Class<? extends GeneralDisDTO> getClassFor(String displayName) {
        return classByDisplayString.get(displayName);
    }

    private Class<? extends GeneralDisDTO> getCommonAncestor(List<String> descriptionTypes) {
        if (descriptionTypes.size() == 0) {
            return GeneralDisDTO.class;
        }
        List<?> test = descriptionTypes.stream().map(this::getClassFor).toList();
        Class<?>[] classArray = test.toArray(new Class<?>[0]);
        Class<? extends GeneralDisDTO> commonAncestor = (Class<? extends GeneralDisDTO>) commonSuperClass(classArray).get(0);
        return commonAncestor;
    }

    @GetMapping("/library/filterOptions")
    public ModelAndView getMethodName(@RequestParam(name = "type", required = false) List<String> descriptionTypes) {
        if (descriptionTypes == null) {
            descriptionTypes = new ArrayList<>();
        }
        ModelAndView model = new ModelAndView("search/filterOptions");
        // Identify common ancestor 
        try {
            Class<? extends GeneralDisDTO> commonAncestor = getCommonAncestor(descriptionTypes);
            model.addObject("commonAncestor", commonAncestor);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return model;
    }

    /* This is called to refresh the the html for displaying a search result */
    @GetMapping("/api/search")
    public ModelAndView getSearchResult(@RequestParam Map<String, String> queryParameters) {
        ModelAndView model = new ModelAndView("search/result");
        model = attachResults(model, queryParameters);
        return model;
    }

    private ModelAndView attachResults(ModelAndView model, Map<String, String> queryParameters) {
        //System.out.println("this could do something based on: " + queryParameters.get("searchterm"));
        // TODO identity the service that will 
        try {
            return generalDisService.getResultsFor(model, queryParameters.get("searchterm"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
