package edu.wisc.wud.games.wud_games_website.game_console;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameConsoleService extends EntityService<GameConsoleRepository, GameConsole, GameConsoleDTO> {

    public GameConsoleService(GameConsoleRepository repository, GameConsoleMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected GameConsole newEntity() {
        return new GameConsole();
    }

    @Override
    public GameConsoleDTO newDTO() {
        return new GameConsoleDTO();
    }
}

