package edu.wisc.wud.games.wud_games_website.video_game_dis;

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
@RequestMapping(value = "/api/videoGameDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class VideoGameDisResource {

    private final VideoGameDisService videoGameDisService;

    public VideoGameDisResource(final VideoGameDisService videoGameDisService) {
        this.videoGameDisService = videoGameDisService;
    }

    @GetMapping
    public ResponseEntity<List<VideoGameDisDTO>> getAllVideoGameDiss() {
        return ResponseEntity.ok(videoGameDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoGameDisDTO> getVideoGameDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(videoGameDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createVideoGameDis(
            @RequestBody @Valid final VideoGameDisDTO videoGameDisDTO) {
        final Long createdId = videoGameDisService.create(videoGameDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateVideoGameDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final VideoGameDisDTO videoGameDisDTO) {
        videoGameDisService.update(id, videoGameDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoGameDis(@PathVariable(name = "id") final Long id) {
        videoGameDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

