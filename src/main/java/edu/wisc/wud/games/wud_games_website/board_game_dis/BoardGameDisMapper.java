package edu.wisc.wud.games.wud_games_website.board_game_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.game_dis.GameDisMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class BoardGameDisMapper extends EntityMapper<BoardGameDis, BoardGameDisDTO> {
    public BoardGameDisMapper(GameDisMapper gameDisMapper) {
        super(gameDisMapper, () -> new BoardGameDis(), () -> new BoardGameDisDTO());
    }

    public BoardGameDisDTO localToDTO(final BoardGameDis boardGameDis,
            final BoardGameDisDTO boardGameDisDTO) {
        boardGameDisDTO.setMinPlaytime(boardGameDis.getMinPlaytime());
        boardGameDisDTO.setMaxPlaytime(boardGameDis.getMaxPlaytime());
        return boardGameDisDTO;
    }

    public BoardGameDis localToEntity(final BoardGameDisDTO boardGameDisDTO,
            final BoardGameDis boardGameDis) {
        boardGameDis.setMinPlaytime(boardGameDisDTO.getMinPlaytime());
        boardGameDis.setMaxPlaytime(boardGameDisDTO.getMaxPlaytime());
        return boardGameDis;
    }
}
