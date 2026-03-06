package edu.wisc.wud.games.wud_games_website.game_console;

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
@RequestMapping(value = "/api/gameConsoles", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameConsoleResource {

    private final GameConsoleService gameConsoleService;

    public GameConsoleResource(final GameConsoleService gameConsoleService) {
        this.gameConsoleService = gameConsoleService;
    }

    @GetMapping
    public ResponseEntity<List<GameConsoleDTO>> getAllGameConsoles() {
        return ResponseEntity.ok(gameConsoleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameConsoleDTO> getGameConsole(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(gameConsoleService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGameConsole(
            @RequestBody @Valid final GameConsoleDTO gameConsoleDTO) {
        final Long createdId = gameConsoleService.create(gameConsoleDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGameConsole(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GameConsoleDTO gameConsoleDTO) {
        gameConsoleService.update(id, gameConsoleDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameConsole(@PathVariable(name = "id") final Long id) {
        gameConsoleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

