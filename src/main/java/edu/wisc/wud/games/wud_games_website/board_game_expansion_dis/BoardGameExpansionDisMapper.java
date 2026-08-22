package edu.wisc.wud.games.wud_games_website.board_game_expansion_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class BoardGameExpansionDisMapper extends EntityMapper<BoardGameExpansionDis, BoardGameExpansionDisDTO> {
    private final BoardGameDisMapper boardGameDisMapper;

    public BoardGameExpansionDisMapper(BoardGameDisMapper boardGameDisMapper) {
        super(boardGameDisMapper, BoardGameExpansionDis.class);
        this.boardGameDisMapper = boardGameDisMapper;
    }

    public BoardGameExpansionDisDTO localToDTO(final BoardGameExpansionDis boardGameExpansionDis,
            final BoardGameExpansionDisDTO boardGameExpansionDisDTO) {
        boardGameExpansionDisDTO.setBaseBoardGameDis(boardGameDisMapper.toDTO(boardGameExpansionDis.getBaseBoardGameDis(), new BoardGameDisDTO()));
        return boardGameExpansionDisDTO;
    }

    public BoardGameExpansionDis localToEntity(final BoardGameExpansionDisDTO boardGameExpansionDisDTO,
            final BoardGameExpansionDis boardGameExpansionDis) {
        boardGameExpansionDis.setBaseBoardGameDis(boardGameDisMapper.toEntity(boardGameExpansionDisDTO.getBaseBoardGameDis(), new BoardGameDis()));
        return boardGameExpansionDis;
    }
}
