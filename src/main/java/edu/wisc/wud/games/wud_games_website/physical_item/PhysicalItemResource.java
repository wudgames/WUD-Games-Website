package edu.wisc.wud.games.wud_games_website.physical_item;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class PhysicalItemResource {

    private final PhysicalItemService physicalItemService;

    public PhysicalItemResource(final PhysicalItemService physicalItemService) {
        this.physicalItemService = physicalItemService;
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER')")
    @GetMapping("/manage/inventory/physicalItems")
    public ModelAndView getMethodName() {
        return new ModelAndView("/manage/inventory/physicalItems");
    }

    @GetMapping("/api/physicalItems")
    public ResponseEntity<List<PhysicalItemDTO>> getAllPhysicalItems() {
        return ResponseEntity.ok(physicalItemService.findAll());
    }

    @GetMapping("/api/physicalItems/{id}")
    public ResponseEntity<PhysicalItemDTO> getPhysicalItem(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(physicalItemService.get(id));
    }

    @PostMapping("/api/physicalItems")
    public ResponseEntity<Long> createPhysicalItem(
            @RequestBody @Valid final PhysicalItemDTO physicalItemDTO) {
        final Long createdId = physicalItemService.create(physicalItemDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/api/physicalItems/{id}")
    public ResponseEntity<Long> updatePhysicalItem(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PhysicalItemDTO physicalItemDTO) {
        physicalItemService.update(id, physicalItemDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/api/physicalItems/{id}")
    public ResponseEntity<Void> deletePhysicalItem(@PathVariable(name = "id") final Long id) {
        physicalItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

