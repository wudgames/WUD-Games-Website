package edu.wisc.wud.games.wud_games_website.board_game_dis;

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
@RequestMapping(value = "/api/boardGameDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class BoardGameDisResource {

    private final BoardGameDisService boardGameDisService;

    public BoardGameDisResource(final BoardGameDisService boardGameDisService) {
        this.boardGameDisService = boardGameDisService;
    }

    @GetMapping
    public ResponseEntity<List<BoardGameDisDTO>> getAllBoardGameDiss() {
        return ResponseEntity.ok(boardGameDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGameDisDTO> getBoardGameDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(boardGameDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createBoardGameDis(
            @RequestBody @Valid final BoardGameDisDTO boardGameDisDTO) {
        final Long createdId = boardGameDisService.create(boardGameDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBoardGameDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BoardGameDisDTO boardGameDisDTO) {
        boardGameDisService.update(id, boardGameDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardGameDis(@PathVariable(name = "id") final Long id) {
        boardGameDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

