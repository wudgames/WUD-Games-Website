package edu.wisc.wud.games.wud_games_website.game_console_dis;

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
@RequestMapping(value = "/api/gameConsoleDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameConsoleDisResource {

    private final GameConsoleDisService gameConsoleDisService;

    public GameConsoleDisResource(final GameConsoleDisService gameConsoleDisService) {
        this.gameConsoleDisService = gameConsoleDisService;
    }

    @GetMapping
    public ResponseEntity<List<GameConsoleDisDTO>> getAllGameConsoleDiss() {
        return ResponseEntity.ok(gameConsoleDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameConsoleDisDTO> getGameConsoleDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(gameConsoleDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGameConsoleDis(
            @RequestBody @Valid final GameConsoleDisDTO gameConsoleDisDTO) {
        final Long createdId = gameConsoleDisService.create(gameConsoleDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGameConsoleDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GameConsoleDisDTO gameConsoleDisDTO) {
        gameConsoleDisService.update(id, gameConsoleDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameConsoleDis(@PathVariable(name = "id") final Long id) {
        gameConsoleDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

