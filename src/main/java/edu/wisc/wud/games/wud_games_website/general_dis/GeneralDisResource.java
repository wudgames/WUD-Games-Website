package edu.wisc.wud.games.wud_games_website.general_dis;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import edu.wisc.wud.games.wud_games_website.elasticsearch.ElasticsearchDocument;
import edu.wisc.wud.games.wud_games_website.elasticsearch.ElasticsearchDocumentRepository;
import edu.wisc.wud.games.wud_games_website.elasticsearch.ElasticsearchDocumentService;

@RestController
public class GeneralDisResource {

    private final GeneralDisService generalDisService;

    @Autowired
    private ElasticsearchDocumentRepository elasticsearchDocumentRepository;

    @Autowired
    private ElasticsearchDocumentService elasticsearchDocumentService;

    public GeneralDisResource(final GeneralDisService generalDisService) {
        this.generalDisService = generalDisService;
    }
    
    @GetMapping("/library")
    public ModelAndView librarySearch(Model model, @RequestParam Map<String, String> queryParameters) {
        attachResults(model, queryParameters);
        return new ModelAndView("search/generaldis");
    }

    /* This is called to refresh the the html for displaying a search result */
    @GetMapping("/api/search")
    public ModelAndView getMethodNameM(Model model, @RequestParam Map<String, String> queryParameters) {
        attachResults(model, queryParameters);
        return new ModelAndView("search/result");
    }

    private void attachResults(Model model, Map<String, String> queryParameters) {
        String searchString = queryParameters.get("searchterm");
        System.out.println("this could do something based on: " + searchString);
        /*
        

        //List<GeneralDisDTO> generalDisDTOList = generalDisService.findAll();
        SearchHits<ElasticsearchDocument> searchHits = elasticsearchDocumentRepository.findByNameOrDescription(query, query);
        List<ElasticsearchDocument> elasticsearchDocumentList = new ArrayList<>();
        for (SearchHit<ElasticsearchDocument> searchHit : searchHits) {
            System.out.println(searchHit.getContent().getName() + " had a score of " + searchHit.getScore());
            elasticsearchDocumentList.add(searchHit.getContent());
        }
        Iterable<ElasticsearchDocument> iterator = elasticsearchDocumentList;
        //Iterable<ElasticsearchDocument> elasticsearchDocumentList = elasticsearchDocumentRepository.findAll();

        */

        List<ElasticsearchDocument> elasticsearchDocumentsList = elasticsearchDocumentService.queryDescriptions(searchString);
        List<GeneralDisDTO> generalDisDTOList = elasticsearchDocumentService.mapAllToGeneralDis(elasticsearchDocumentsList);

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

