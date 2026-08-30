package edu.wisc.wud.games.wud_games_website.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordService;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@EnableMethodSecurity
@PreAuthorize("hasRole('HOST')")
public class CheckoutRecordResource {

    private final CheckoutRecordService checkoutRecordService;
    private final InventoryItemService inventoryItemService;

    public CheckoutRecordResource(final CheckoutRecordService checkoutRecordService, final InventoryItemService inventoryItemService) {
        this.checkoutRecordService = checkoutRecordService;
        this.inventoryItemService = inventoryItemService;
    }
    /*
    // To be used to making a checkout based on description id, like from a search result
    @GetMapping("/manage/checkout")
    public ModelAndView getCheckoutPage(@RequestParam(required = false) Long description_id) {
        // UI and displays the items that are going to be added to the checkout record.
        // Ability to add edit and remove from the items to be included.
        System.out.println("Displaying items for checkout");
        ModelAndView model = new ModelAndView("manage/checkouts/checkoutUpdater");
        CheckoutRecordDTO dto;
        if (false) {
            // Check if checkout record exists, if not then create a new one
        } else {
            model.addObject("checkoutRecord", new CheckoutRecordDTO());
        }
        return model;
    }
    */
    // Called when starting a checkout based on items id(s), like when from the items table
    @GetMapping("/manage/checkout")
    public ModelAndView getCheckoutPage(@RequestParam(required = true) List<Long> item_id) {
        ModelAndView model = new ModelAndView("manage/checkouts/checkoutUpdater");
        CheckoutRecordDTO dto = new CheckoutRecordDTO();
        dto.setInventoryItems(inventoryItemService.findAllById(item_id.stream()));
        model.addObject("checkoutRecord", dto);
        return model;
    }
    
    @PostMapping("/manage/checkout")
    public ModelAndView updateCheckout(@RequestParam Map<String, String> params, @RequestParam(name = "item_id") Set<String> item_ids, @ModelAttribute CheckoutRecordDTO checkoutRecordDTO) {
        Stream<Long> itemIdsStream = item_ids.stream().map(id -> Long.valueOf(id));
        Boolean markReturned = params.get("markReturned") != null && params.get("markReturned").equals("true");
        try {
            return checkoutRecordService.updateCheckout(checkoutRecordDTO, itemIdsStream, markReturned);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    @PostMapping("/manage/checkout/return")
    public ModelAndView postMethodName(@RequestBody Long checkout_id) {
        return checkoutRecordService.markReturned(checkout_id);
    }
    

}

