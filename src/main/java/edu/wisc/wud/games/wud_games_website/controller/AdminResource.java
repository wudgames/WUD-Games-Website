package edu.wisc.wud.games.wud_games_website.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import edu.wisc.wud.games.wud_games_website.user_account.UserAccountService;
import edu.wisc.wud.games.wud_games_website.util.CsvService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AdminResource {
    
    private final UserAccountService userAccountService;
    private final CsvService csvService;

    public AdminResource(UserAccountService userAccountService, CsvService csvService) {
        this.userAccountService = userAccountService;
        this.csvService = csvService;
    }

    @GetMapping("/manage/admin")
    public ModelAndView getDashboard() {
        ModelAndView model = new ModelAndView("manage/admin/adminDashboard");
        return model;
    }

    @PostMapping("/manage/admin/upload")
    public ModelAndView uploadCSV(@RequestParam MultipartFile file) {
        ModelAndView model = new ModelAndView("manage/admin/adminDashboard");
        System.out.println("file was uploaded");
        // legacy compatibility parsing:
        try {
            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                csvService.importBoardGamesFromCsv(reader);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (CsvException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    @GetMapping("/manage/admin/userAccounts")
    public ModelAndView getUserAccountResults(@RequestParam String email) {
        ModelAndView model = new ModelAndView("manage/admin/userAccountsTable");
        model.addObject("users", userAccountService.emailContains(email));
        return model;
    }
    
    
}
