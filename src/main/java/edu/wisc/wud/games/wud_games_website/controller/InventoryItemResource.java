package edu.wisc.wud.games.wud_games_website.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.account.AccountDTO;
import edu.wisc.wud.games.wud_games_website.board_game.BoardGameDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion.BoardGameExpansionDTO;
import edu.wisc.wud.games.wud_games_website.console_account.ConsoleAccountDTO;
import edu.wisc.wud.games.wud_games_website.equipment.EquipmentDTO;
import edu.wisc.wud.games.wud_games_website.game_console.GameConsoleDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemService;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemDTO;
import edu.wisc.wud.games.wud_games_website.steam_account.SteamAccountDTO;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.video_game.VideoGameDTO;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class InventoryItemResource {

    private final InventoryItemService inventoryItemService;
    private final Map<Class<? extends InventoryItemDTO>, Supplier<InventoryItemDTO>> itemDTOSupplierMap = new HashMap<>();

    public InventoryItemResource(final InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;

        // Physical Items
        itemDTOSupplierMap.put(BoardGameDTO.class, () -> new BoardGameDTO());
        itemDTOSupplierMap.put(BoardGameExpansionDTO.class, () -> new BoardGameExpansionDTO());

        itemDTOSupplierMap.put(EquipmentDTO.class, () -> new EquipmentDTO());
        itemDTOSupplierMap.put(GameConsoleDTO.class, () -> new GameConsoleDTO());

        // Digital Items
        itemDTOSupplierMap.put(VideoGameDTO.class, () -> new VideoGameDTO());
        //itemDTOSupplierMap.put(BoardGameDTO.class, () -> new BoardGameDTO());

        itemDTOSupplierMap.put(AccountDTO.class, () -> new AccountDTO());
        itemDTOSupplierMap.put(SteamAccountDTO.class, () -> new SteamAccountDTO());
        itemDTOSupplierMap.put(ConsoleAccountDTO.class, () -> new ConsoleAccountDTO());
    }

    @GetMapping("/library/itemsTable")
    public ModelAndView getItemsTableFor(@RequestParam Long description_id) {
        return inventoryItemService.getInventoryItemsFor(description_id);
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @PostMapping("/api/manage/createItem")
    public ModelAndView createInventoryItem(@RequestParam Long description_id, HttpServletRequest request) {
        try {
            inventoryItemService.createItemFor(description_id, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("search/singleDescription");
    }
    
    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("/manage/inventoryItem/edit")
    public ModelAndView getMethodName(@RequestParam Long item_id) {
        InventoryItemDTO item = inventoryItemService.get(item_id);
        System.out.println("Editing" + item.getClass());
        // TODO check authority
        ModelAndView modelAndView = new ModelAndView("manage/items/editItem");
        modelAndView.addObject("item", item);
        return modelAndView;
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @PostMapping("/manage/inventoryItem/edit")
    public ModelAndView updateItem(@RequestParam Map<String, String> parameters) {
        // TODO move whole method to service
        InventoryItemDTO originalItem = inventoryItemService.get(Long.valueOf(parameters.get("id")));
        // TODO check authority
        InventoryItemDTO updatedItem = updateItem(originalItem, parameters);
        inventoryItemService.create(updatedItem);
        return new ModelAndView("redirect:/library/" + updatedItem.getGenDis().getId());
    }

    private InventoryItemDTO updateItem(InventoryItemDTO originalItem, Map<String, String> parameters) {
        InventoryItemDTO updatedItem = itemDTOSupplierMap.get(originalItem.getClass()).get();
        updatedItem.setId(originalItem.getId());
        updatedItem.setGenDis(originalItem.getGenDis());
        updatedItem.setDateAdded(originalItem.getDateAdded());
        updatedItem.setNotes(parameters.get("notes"));
        // description is skipped
        if (updatedItem instanceof PhysicalItemDTO) {
            if (parameters.containsKey("barcode") && !parameters.get("barcode").isBlank()) {
                ((PhysicalItemDTO) updatedItem).setBarcode(Long.valueOf(parameters.get("barcode")));
            } else {
                ((PhysicalItemDTO) updatedItem).setBarcode(((PhysicalItemDTO) originalItem).getBarcode());
            }
            // TODO Updating the location (skiped for now)
            ((PhysicalItemDTO) updatedItem).setLocation(((PhysicalItemDTO) originalItem).getLocation());
        }
        // TODO Barcodes and accounts
        return updatedItem;
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @PostMapping("/api/manage/deleteItem")
    public ModelAndView postMethodName(@RequestParam Map<String, String> parameters) {
        // TODO move whole method to service
        // TODO check authority
        Long item_id = Long.valueOf(parameters.get("item_id"));
        Long descriptionId = inventoryItemService.get(item_id).getGenDis().getId();
        try {
            inventoryItemService.delete(item_id);// Failing with no such element found exception
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No such item exists");
        }
        return new ModelAndView("redirect:/library/" + descriptionId);
    }
}

