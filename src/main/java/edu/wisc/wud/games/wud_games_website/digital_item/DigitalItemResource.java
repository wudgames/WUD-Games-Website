package edu.wisc.wud.games.wud_games_website.digital_item;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/digitalItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class DigitalItemResource {

    private final DigitalItemService digitalItemService;

    public DigitalItemResource(final DigitalItemService digitalItemService) {
        this.digitalItemService = digitalItemService;
    }

    @GetMapping
    public ResponseEntity<List<DigitalItemDTO>> getAllDigitalItems() {
        return ResponseEntity.ok(digitalItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DigitalItemDTO> getDigitalItem(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(digitalItemService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createDigitalItem(
            @RequestBody @Valid final DigitalItemDTO digitalItemDTO) {
        final Long createdId = digitalItemService.create(digitalItemDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDigitalItem(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DigitalItemDTO digitalItemDTO) {
        digitalItemService.update(id, digitalItemDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDigitalItem(@PathVariable(name = "id") final Long id) {
        digitalItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

