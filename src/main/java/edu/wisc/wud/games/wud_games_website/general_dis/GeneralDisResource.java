package edu.wisc.wud.games.wud_games_website.general_dis;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;



@RestController
public class GeneralDisResource {

    private final GeneralDisService generalDisService;

    public GeneralDisResource(final GeneralDisService generalDisService) {
        this.generalDisService = generalDisService;
    }
    
    @GetMapping("/library")
    public ModelAndView librarySearch(Model model) {
        List<GeneralDisDTO> generalDisDTOList = generalDisService.findAll();
        model.addAttribute("generalDisDTOList", generalDisDTOList);
        System.out.println(generalDisDTOList);
        return new ModelAndView("search/generaldis");
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

