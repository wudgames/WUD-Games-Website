package edu.wisc.wud.games.wud_games_website.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;

@RestController
public class InventoryDisplayResource {
    private final GeneralDisService generalDisService;

    public InventoryDisplayResource(GeneralDisService generalDisService) {
        this.generalDisService = generalDisService;
    }

    @GetMapping("/library")
    public ModelAndView librarySearch(@RequestParam Map<String, String> queryParameters) {
        ModelAndView model = new ModelAndView("search/library");
        attachResults(model, queryParameters);
        return model;
    }

    @GetMapping("/library/{id}/{vanity_name}")
    public ModelAndView getPageForDescription(@PathVariable Long id) {
        ModelAndView model = new ModelAndView("search/singleDescription");
        GeneralDisDTO generalDisDTO = generalDisService.get(id);
        model.addObject("description", generalDisDTO);
        return model;
    }

    @GetMapping("/library/{id}")
    public ModelAndView getPageByIdOnly(@PathVariable Long id) {
        return getPageForDescription(id);
    }

    /* This is called to refresh the the html for displaying a search result */
    @GetMapping("/api/search")
    public ModelAndView getSearchResult(@RequestParam Map<String, String> queryParameters) {
        ModelAndView model = new ModelAndView("search/result");
        attachResults(model, queryParameters);
        return model;
    }

    private void attachResults(ModelAndView model, Map<String, String> queryParameters) {
        System.out.println("this could do something based on: " + queryParameters.get("searchterm"));
        List<GeneralDisDTO> generalDisDTOList = generalDisService.findAll();
        model.addObject("generalDisDTOList", generalDisDTOList);
    }
}
