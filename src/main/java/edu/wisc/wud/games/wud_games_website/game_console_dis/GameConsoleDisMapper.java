package edu.wisc.wud.games.wud_games_website.game_console_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;

@Component
public class GameConsoleDisMapper extends EntityMapper<GameConsoleDis, GameConsoleDisDTO> {

    public GameConsoleDisMapper(GeneralDisMapper parentMapper) {
        super(parentMapper, () -> new GameConsoleDis(), () -> new GameConsoleDisDTO());
    }

    @Override
    protected GameConsoleDisDTO localToDTO(GameConsoleDis entity, GameConsoleDisDTO dto) {
        return dto;
    }

    @Override
    protected GameConsoleDis localToEntity(GameConsoleDisDTO dto, GameConsoleDis entity) {
        return entity;
    }
    
}
