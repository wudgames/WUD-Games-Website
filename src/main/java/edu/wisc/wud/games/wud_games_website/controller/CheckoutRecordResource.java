package edu.wisc.wud.games.wud_games_website.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordService;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@EnableMethodSecurity
@PreAuthorize("hasRole('HOST')")
@RequestMapping(value = "/api/checkoutRecords", produces = MediaType.APPLICATION_JSON_VALUE)
public class CheckoutRecordResource {

    private final CheckoutRecordService checkoutRecordService;

    public CheckoutRecordResource(final CheckoutRecordService checkoutRecordService) {
        this.checkoutRecordService = checkoutRecordService;
    }

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    

}

