package edu.wisc.wud.games.wud_games_website.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemService;

import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class InventoryItemResource {

    private final InventoryItemService inventoryItemService;

    public InventoryItemResource(final InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping("/library/itemsTable")
    public ModelAndView getItemsTableFor(@RequestParam Long description_id) {
        return inventoryItemService.getInventoryItemsFor(description_id);
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER')")
    @PostMapping("/api/manage/createItem")
    public void getMethodName(@RequestParam Long description_id, HttpServletRequest request) {
        inventoryItemService.createItemFor(description_id, request);
    }
    

}

