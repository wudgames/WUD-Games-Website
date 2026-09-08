package edu.wisc.wud.games.wud_games_website.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.account.AccountDTO;
import edu.wisc.wud.games.wud_games_website.account_dis.AccountDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game.BoardGameDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion.BoardGameExpansionDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.console_account.ConsoleAccountDTO;
import edu.wisc.wud.games.wud_games_website.console_account_dis.ConsoleAccountDisDTO;
import edu.wisc.wud.games.wud_games_website.equipment.EquipmentDTO;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.game_console.GameConsoleDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemService;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemDTO;
import edu.wisc.wud.games.wud_games_website.steam_account.SteamAccountDTO;
import edu.wisc.wud.games.wud_games_website.steam_account_dis.SteamAccountDisDTO;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.video_game.VideoGameDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class InventoryItemResource {

    private final InventoryItemService inventoryItemService;
    private final Map<Class<? extends GeneralDisDTO>, Supplier<InventoryItemDTO>> itemDTOSupplierMap = new HashMap<>();

    private final GeneralDisMapper generalDisMapper;

    public InventoryItemResource(final InventoryItemService inventoryItemService, final GeneralDisMapper generalDisMapper) {
        this.inventoryItemService = inventoryItemService;
        this.generalDisMapper = generalDisMapper;

        // Physical Items
        itemDTOSupplierMap.put(BoardGameDisDTO.class, () -> new BoardGameDTO());
        itemDTOSupplierMap.put(BoardGameExpansionDisDTO.class, () -> new BoardGameExpansionDTO());

        itemDTOSupplierMap.put(EquipmentDisDTO.class, () -> new EquipmentDTO());
        itemDTOSupplierMap.put(GameConsoleDisDTO.class, () -> new GameConsoleDTO());

        // Digital Items
        itemDTOSupplierMap.put(VideoGameDisDTO.class, () -> new VideoGameDTO());
        //itemDTOSupplierMap.put(BoardGameDisDTO.class, () -> new BoardGameDTO());

        itemDTOSupplierMap.put(AccountDisDTO.class, () -> new AccountDTO());
        itemDTOSupplierMap.put(SteamAccountDisDTO.class, () -> new SteamAccountDTO());
        itemDTOSupplierMap.put(ConsoleAccountDisDTO.class, () -> new ConsoleAccountDTO());
    }

    @GetMapping("/library/itemsTable")
    public ModelAndView getItemsTableFor(@RequestParam Long description_id) {
        return inventoryItemService.getInventoryItemsFor(description_id);
    }

    @PreAuthorize("hasRole('PHYSICAL_INVENTORY_MANAGER') or hasRole('DIGITAL_INVENTORY_MANAGER')")
    @GetMapping("/api/manage/createItem")
    public ModelAndView createInventoryItem(@RequestParam Long description_id, HttpServletRequest request) {
        return inventoryItemService.getPageToCreateItemFor(description_id, request);
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
    public ModelAndView updateItem(@RequestParam Map<String, String> parameters, @RequestParam Long description_id, @RequestParam(required = false) String locationsName) {
        // Using module attribute was causing an issues because item did not including fields that are on subclasses of InventoryItemDTO
        return inventoryItemService.updatedOrCreate(parameters, locationsName, description_id);
        //inventoryItemService.create(item);
        //return new ModelAndView("redirect:/library/" + item.getGenDis().getId());
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

