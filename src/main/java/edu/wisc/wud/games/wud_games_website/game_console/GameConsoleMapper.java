package edu.wisc.wud.games.wud_games_website.game_console;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemMapper;

@Component
public class GameConsoleMapper extends EntityMapper<GameConsole, GameConsoleDTO> {

    public GameConsoleMapper(PhysicalItemMapper parentMapper) {
        super(parentMapper, () -> new GameConsole(), () -> new GameConsoleDTO());
    }

    @Override
    protected GameConsoleDTO localToDTO(GameConsole entity, GameConsoleDTO dto) {
        return dto;
    }

    @Override
    protected GameConsole localToEntity(GameConsoleDTO dto, GameConsole entity) {
        return entity;
    }
    
}
