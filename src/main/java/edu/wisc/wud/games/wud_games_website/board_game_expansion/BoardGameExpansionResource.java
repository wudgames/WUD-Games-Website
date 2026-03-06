package edu.wisc.wud.games.wud_games_website.board_game_expansion;

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
@RequestMapping(value = "/api/boardGameExpansions", produces = MediaType.APPLICATION_JSON_VALUE)
public class BoardGameExpansionResource {

    private final BoardGameExpansionService boardGameExpansionService;

    public BoardGameExpansionResource(final BoardGameExpansionService boardGameExpansionService) {
        this.boardGameExpansionService = boardGameExpansionService;
    }

    @GetMapping
    public ResponseEntity<List<BoardGameExpansionDTO>> getAllBoardGameExpansions() {
        return ResponseEntity.ok(boardGameExpansionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardGameExpansionDTO> getBoardGameExpansion(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(boardGameExpansionService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createBoardGameExpansion(
            @RequestBody @Valid final BoardGameExpansionDTO boardGameExpansionDTO) {
        final Long createdId = boardGameExpansionService.create(boardGameExpansionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBoardGameExpansion(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BoardGameExpansionDTO boardGameExpansionDTO) {
        boardGameExpansionService.update(id, boardGameExpansionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoardGameExpansion(@PathVariable(name = "id") final Long id) {
        boardGameExpansionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

