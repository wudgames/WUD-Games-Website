package edu.wisc.wud.games.wud_games_website.game_console_dis;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GameConsoleDisService extends EntityService<GameConsoleDisRepository, GameConsoleDis, GameConsoleDisDTO> {
    public GameConsoleDisService(GameConsoleDisRepository repository,
            GameConsoleDisMapper mapper, ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    public Map<Long, Long> getGameConsoleDisValues() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GameConsoleDis::getId, GameConsoleDis::getId));
    }

    @Override
    protected GameConsoleDis newEntity() {
        return new GameConsoleDis();
    }

    @Override
    public GameConsoleDisDTO newDTO() {
        return new GameConsoleDisDTO();
    }

}

