package edu.wisc.wud.games.wud_games_website.game_dis;

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
@RequestMapping(value = "/api/gameDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class GameDisResource {

    private final GameDisService gameDisService;

    public GameDisResource(final GameDisService gameDisService) {
        this.gameDisService = gameDisService;
    }

    @GetMapping
    public ResponseEntity<List<GameDisDTO>> getAllGameDiss() {
        return ResponseEntity.ok(gameDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDisDTO> getGameDis(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(gameDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGameDis(@RequestBody @Valid final GameDisDTO gameDisDTO) {
        final Long createdId = gameDisService.create(gameDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGameDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GameDisDTO gameDisDTO) {
        gameDisService.update(id, gameDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameDis(@PathVariable(name = "id") final Long id) {
        gameDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

