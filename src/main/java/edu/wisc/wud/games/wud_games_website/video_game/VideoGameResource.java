package edu.wisc.wud.games.wud_games_website.video_game;

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
@RequestMapping(value = "/api/videoGames", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoGameResource {

    private final VideoGameService videoGameService;

    public VideoGameResource(final VideoGameService videoGameService) {
        this.videoGameService = videoGameService;
    }

    @GetMapping
    public ResponseEntity<List<VideoGameDTO>> getAllVideoGames() {
        return ResponseEntity.ok(videoGameService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoGameDTO> getVideoGame(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(videoGameService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createVideoGame(
            @RequestBody @Valid final VideoGameDTO videoGameDTO) {
        final Long createdId = videoGameService.create(videoGameDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateVideoGame(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final VideoGameDTO videoGameDTO) {
        videoGameService.update(id, videoGameDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoGame(@PathVariable(name = "id") final Long id) {
        videoGameService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

