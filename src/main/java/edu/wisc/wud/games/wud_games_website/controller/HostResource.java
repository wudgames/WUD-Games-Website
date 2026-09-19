package edu.wisc.wud.games.wud_games_website.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@PreAuthorize("hasRole('HOST')")
public class HostResource {
    private final CheckoutRecordService checkoutRecordService;

    public HostResource(CheckoutRecordService checkoutRecordService) {
        this.checkoutRecordService = checkoutRecordService;
    }

    @GetMapping("/host/dashboard")
    public ModelAndView getDashboard() {
        return checkoutRecordService.getHostDashboard();
    }
    
}
