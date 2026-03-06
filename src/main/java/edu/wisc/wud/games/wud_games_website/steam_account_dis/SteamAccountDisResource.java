package edu.wisc.wud.games.wud_games_website.steam_account_dis;

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


@RestController
@RequestMapping(value = "/api/steamAccountDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class SteamAccountDisResource {

    private final SteamAccountDisService steamAccountDisService;

    public SteamAccountDisResource(final SteamAccountDisService steamAccountDisService) {
        this.steamAccountDisService = steamAccountDisService;
    }

    @GetMapping
    public ResponseEntity<List<SteamAccountDisDTO>> getAllSteamAccountDiss() {
        return ResponseEntity.ok(steamAccountDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SteamAccountDisDTO> getSteamAccountDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(steamAccountDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createSteamAccountDis(
            @RequestBody @Valid final SteamAccountDisDTO steamAccountDisDTO) {
        final Long createdId = steamAccountDisService.create(steamAccountDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSteamAccountDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SteamAccountDisDTO steamAccountDisDTO) {
        steamAccountDisService.update(id, steamAccountDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSteamAccountDis(@PathVariable(name = "id") final Long id) {
        steamAccountDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

