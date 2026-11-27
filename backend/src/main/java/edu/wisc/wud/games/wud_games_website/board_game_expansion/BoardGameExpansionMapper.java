package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.board_game.BoardGameMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class BoardGameExpansionMapper extends EntityMapper<BoardGameExpansion, BoardGameExpansionDTO> {
    public BoardGameExpansionMapper(BoardGameMapper boardGameMapper) {
        super(boardGameMapper,() -> new BoardGameExpansion(), () -> new BoardGameExpansionDTO());
    }

    @Override
    public BoardGameExpansionDTO localToDTO(BoardGameExpansion entity, BoardGameExpansionDTO dto) {
        return dto;
    }

    @Override
    public BoardGameExpansion localToEntity(BoardGameExpansionDTO dto, BoardGameExpansion entity) {
        return entity;
    }
    
}
