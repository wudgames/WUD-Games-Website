package edu.wisc.wud.games.wud_games_website.board_game;

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
@RequestMapping(value = "/api/boardGames", produces = MediaType.APPLICATION_JSON_VALUE)
public class BoardGameResource {

    private final BoardGameService boardGameService;

    public BoardGameResource(final BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @GetMapping
    public ResponseEntity<List<BoardGameDTO>> getAllBoardGames() {
        return ResponseEntity.ok(boardGameService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGameDTO> getBoardGame(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(boardGameService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createBoardGame(
            @RequestBody @Valid final BoardGameDTO boardGameDTO) {
        final Long createdId = boardGameService.create(boardGameDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBoardGame(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BoardGameDTO boardGameDTO) {
        boardGameService.update(id, boardGameDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardGame(@PathVariable(name = "id") final Long id) {
        boardGameService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

