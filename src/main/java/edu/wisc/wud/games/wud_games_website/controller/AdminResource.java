package edu.wisc.wud.games.wud_games_website.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.wisc.wud.games.wud_games_website.user_account.UserAccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AdminResource {
    
    private final UserAccountService userAccountService;

    public AdminResource(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/manage/admin")
    public ModelAndView getDashboard() {
        ModelAndView model = new ModelAndView("manage/admin/adminDashboard");
        return model;
    }

    @GetMapping("/manage/admin/userAccounts")
    public ModelAndView getUserAccountResults(@RequestParam String email) {
        ModelAndView model = new ModelAndView("manage/admin/adminDashboard");
        model.addObject("users", userAccountService.findByEmail(email));
        return model;
    }
    
    
}
