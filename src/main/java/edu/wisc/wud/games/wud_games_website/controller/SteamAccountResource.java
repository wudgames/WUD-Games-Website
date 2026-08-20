package edu.wisc.wud.games.wud_games_website.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.wisc.wud.games.wud_games_website.steam_account.SteamAccountDTO;
import edu.wisc.wud.games.wud_games_website.steam_account.SteamAccountService;


@RestController
@RequestMapping(value = "/api/steamAccounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class SteamAccountResource {

    private final SteamAccountService steamAccountService;

    public SteamAccountResource(final SteamAccountService steamAccountService) {
        this.steamAccountService = steamAccountService;
    }

    @GetMapping
    public ResponseEntity<List<SteamAccountDTO>> getAllSteamAccounts() {
        return ResponseEntity.ok(steamAccountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SteamAccountDTO> getSteamAccount(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(steamAccountService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createSteamAccount(
            @RequestBody @Valid final SteamAccountDTO steamAccountDTO) {
        final Long createdId = steamAccountService.create(steamAccountDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSteamAccount(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SteamAccountDTO steamAccountDTO) {
        steamAccountService.update(id, steamAccountDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSteamAccount(@PathVariable(name = "id") final Long id) {
        steamAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

