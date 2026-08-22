package edu.wisc.wud.games.wud_games_website.general_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisMapper;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisMapper;

@Component
public class GeneralDisSubclassMapper {
    private final BoardGameExpansionDisMapper boardGameExpansionDisMapper;
    private final BoardGameDisMapper boardGameDisMapper;
    //private final VideoGameExpansionDisMapper videoGameExpansionDisMapper;
    //private final VideoGameDisMapper videoGameDisMapper;
    //private final EquipmentDisMapper equipmentDisMapper;
    //private final GameConsoleDisMapper gameConsoleDisMapper;
    //private final AccountDisMapper accountDisMapper;
    
    public GeneralDisSubclassMapper(BoardGameExpansionDisMapper boardGameExpansionDisMapper, BoardGameDisMapper boardGameDisMapper) {
        
        this.boardGameExpansionDisMapper = boardGameExpansionDisMapper;
        this.boardGameDisMapper = boardGameDisMapper;
    }

    public GeneralDisDTO toDTO(final GeneralDis generalDis) {
        throw new UnsupportedOperationException("This method has not been implemented.");
    }

    public GeneralDis toEntity(final GeneralDisDTO dto) {
        if (dto instanceof BoardGameExpansionDisDTO) {
            return boardGameExpansionDisMapper.toEntity((BoardGameExpansionDisDTO) dto);
        } else if (dto instanceof BoardGameDisDTO) {
            return boardGameDisMapper.toEntity((BoardGameDisDTO) dto);
        }
        throw new UnsupportedOperationException("This method has not been implemented.");
    }
}
