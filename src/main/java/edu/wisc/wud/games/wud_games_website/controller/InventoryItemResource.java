package edu.wisc.wud.games.wud_games_website.controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemService;

import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class InventoryItemResource {

    private final InventoryItemService inventoryItemService;

    public InventoryItemResource(final InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping("/api/inventoryItems")
    public ResponseEntity<List<InventoryItemDTO>> getAllInventoryItems() {
        return ResponseEntity.ok(inventoryItemService.findAll());
    }

    @GetMapping("/api/inventoryItems/{id}")
    public ResponseEntity<InventoryItemDTO> getInventoryItem(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(inventoryItemService.get(id));
    }

    @PostMapping("/api/inventoryItems")
    public ResponseEntity<Long> createInventoryItem(
            @RequestBody @Valid final InventoryItemDTO inventoryItemDTO) {
        final Long createdId = inventoryItemService.create(inventoryItemDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/api/inventoryItems/{id}")
    public ResponseEntity<Long> updateInventoryItem(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final InventoryItemDTO inventoryItemDTO) {
        inventoryItemService.update(id, inventoryItemDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/api/inventoryItems/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable(name = "id") final Long id) {
        inventoryItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

