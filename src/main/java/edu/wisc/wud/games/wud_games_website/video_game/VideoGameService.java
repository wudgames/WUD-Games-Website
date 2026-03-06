package edu.wisc.wud.games.wud_games_website.video_game;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteVideoGameDis;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDis;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisRepository;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class VideoGameService {

    private final VideoGameRepository videoGameRepository;
    private final VideoGameDisRepository videoGameDisRepository;

    public VideoGameService(final VideoGameRepository videoGameRepository,
            final VideoGameDisRepository videoGameDisRepository) {
        this.videoGameRepository = videoGameRepository;
        this.videoGameDisRepository = videoGameDisRepository;
    }

    public List<VideoGameDTO> findAll() {
        final List<VideoGame> videoGames = videoGameRepository.findAll(Sort.by("id"));
        return videoGames.stream()
                .map(videoGame -> mapToDTO(videoGame, new VideoGameDTO()))
                .toList();
    }

    public VideoGameDTO get(final Long id) {
        return videoGameRepository.findById(id)
                .map(videoGame -> mapToDTO(videoGame, new VideoGameDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VideoGameDTO videoGameDTO) {
        final VideoGame videoGame = new VideoGame();
        mapToEntity(videoGameDTO, videoGame);
        return videoGameRepository.save(videoGame).getId();
    }

    public void update(final Long id, final VideoGameDTO videoGameDTO) {
        final VideoGame videoGame = videoGameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(videoGameDTO, videoGame);
        videoGameRepository.save(videoGame);
    }

    public void delete(final Long id) {
        final VideoGame videoGame = videoGameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        videoGameRepository.delete(videoGame);
    }

    private VideoGameDTO mapToDTO(final VideoGame videoGame, final VideoGameDTO videoGameDTO) {
        videoGameDTO.setId(videoGame.getId());
        videoGameDTO.setVideoGameDis(videoGame.getVideoGameDis() == null ? null : videoGame.getVideoGameDis().getId());
        return videoGameDTO;
    }

    private VideoGame mapToEntity(final VideoGameDTO videoGameDTO, final VideoGame videoGame) {
        final VideoGameDis videoGameDis = videoGameDTO.getVideoGameDis() == null ? null : videoGameDisRepository.findById(videoGameDTO.getVideoGameDis())
                .orElseThrow(() -> new NotFoundException("videoGameDis not found"));
        videoGame.setVideoGameDis(videoGameDis);
        return videoGame;
    }

    @EventListener(BeforeDeleteVideoGameDis.class)
    public void on(final BeforeDeleteVideoGameDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final VideoGame videoGameDisVideoGame = videoGameRepository.findFirstByVideoGameDisId(event.getId());
        if (videoGameDisVideoGame != null) {
            referencedException.setKey("videoGameDis.videoGame.videoGameDis.referenced");
            referencedException.addParam(videoGameDisVideoGame.getId());
            throw referencedException;
        }
    }

}

