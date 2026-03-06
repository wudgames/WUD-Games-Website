package edu.wisc.wud.games.wud_games_website.game_expansion_dis;

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
@RequestMapping(value = "/api/gameExpansionDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameExpansionDisResource {

    private final GameExpansionDisService gameExpansionDisService;

    public GameExpansionDisResource(final GameExpansionDisService gameExpansionDisService) {
        this.gameExpansionDisService = gameExpansionDisService;
    }

    @GetMapping
    public ResponseEntity<List<GameExpansionDisDTO>> getAllGameExpansionDiss() {
        return ResponseEntity.ok(gameExpansionDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameExpansionDisDTO> getGameExpansionDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(gameExpansionDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGameExpansionDis(
            @RequestBody @Valid final GameExpansionDisDTO gameExpansionDisDTO) {
        final Long createdId = gameExpansionDisService.create(gameExpansionDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGameExpansionDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GameExpansionDisDTO gameExpansionDisDTO) {
        gameExpansionDisService.update(id, gameExpansionDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameExpansionDis(@PathVariable(name = "id") final Long id) {
        gameExpansionDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

