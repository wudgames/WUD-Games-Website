package edu.wisc.wud.games.wud_games_website.physical_item;

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
@RequestMapping(value = "/api/physicalItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class PhysicalItemResource {

    private final PhysicalItemService physicalItemService;

    public PhysicalItemResource(final PhysicalItemService physicalItemService) {
        this.physicalItemService = physicalItemService;
    }

    @GetMapping
    public ResponseEntity<List<PhysicalItemDTO>> getAllPhysicalItems() {
        return ResponseEntity.ok(physicalItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhysicalItemDTO> getPhysicalItem(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(physicalItemService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createPhysicalItem(
            @RequestBody @Valid final PhysicalItemDTO physicalItemDTO) {
        final Long createdId = physicalItemService.create(physicalItemDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePhysicalItem(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PhysicalItemDTO physicalItemDTO) {
        physicalItemService.update(id, physicalItemDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhysicalItem(@PathVariable(name = "id") final Long id) {
        physicalItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

