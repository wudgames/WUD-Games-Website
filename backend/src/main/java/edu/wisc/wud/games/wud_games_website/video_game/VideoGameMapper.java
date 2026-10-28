package edu.wisc.wud.games.wud_games_website.video_game;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.digital_item.DigitalItemMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class VideoGameMapper extends EntityMapper<VideoGame, VideoGameDTO> {

    public VideoGameMapper(DigitalItemMapper parentMapper) {
        super(parentMapper, () -> new VideoGame(), () -> new VideoGameDTO());
    }

    @Override
    protected VideoGameDTO localToDTO(VideoGame entity, VideoGameDTO dto) {
        return dto;
    }

    @Override
    protected VideoGame localToEntity(VideoGameDTO dto, VideoGame entity) {
        return entity;
    }
    
}
