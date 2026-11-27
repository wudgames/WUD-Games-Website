package edu.wisc.wud.games.wud_games_website.board_game;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemMapper;

@Component
public class BoardGameMapper extends EntityMapper<BoardGame, BoardGameDTO> {
    public BoardGameMapper(PhysicalItemMapper physicalItemMapper) {
        super(physicalItemMapper, () -> new BoardGame(), () -> new BoardGameDTO());
    }

    @Override
    public BoardGameDTO localToDTO(BoardGame entity, BoardGameDTO dto) {
        return dto;
    }

    @Override
    public BoardGame localToEntity(BoardGameDTO dto, BoardGame entity) {
        return entity;
    }
    
}
