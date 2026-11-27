package edu.wisc.wud.games.wud_games_website.video_game;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
public class VideoGameService extends EntityService<VideoGameRepository, VideoGame, VideoGameDTO> {

    public VideoGameService(VideoGameRepository repository, VideoGameMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected VideoGame newEntity() {
        return new VideoGame();
    }

    @Override
    public VideoGameDTO newDTO() {
        return new VideoGameDTO();
    }

}

