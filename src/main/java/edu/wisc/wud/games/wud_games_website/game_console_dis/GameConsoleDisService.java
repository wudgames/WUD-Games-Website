package edu.wisc.wud.games.wud_games_website.game_console_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameConsoleDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GameConsoleDisService {

    private final GameConsoleDisRepository gameConsoleDisRepository;
    private final ApplicationEventPublisher publisher;

    public GameConsoleDisService(final GameConsoleDisRepository gameConsoleDisRepository,
            final ApplicationEventPublisher publisher) {
        this.gameConsoleDisRepository = gameConsoleDisRepository;
        this.publisher = publisher;
    }

    public List<GameConsoleDisDTO> findAll() {
        final List<GameConsoleDis> gameConsoleDises = gameConsoleDisRepository.findAll(Sort.by("id"));
        return gameConsoleDises.stream()
                .map(gameConsoleDis -> mapToDTO(gameConsoleDis, new GameConsoleDisDTO()))
                .toList();
    }

    public GameConsoleDisDTO get(final Long id) {
        return gameConsoleDisRepository.findById(id)
                .map(gameConsoleDis -> mapToDTO(gameConsoleDis, new GameConsoleDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GameConsoleDisDTO gameConsoleDisDTO) {
        final GameConsoleDis gameConsoleDis = new GameConsoleDis();
        mapToEntity(gameConsoleDisDTO, gameConsoleDis);
        return gameConsoleDisRepository.save(gameConsoleDis).getId();
    }

    public void update(final Long id, final GameConsoleDisDTO gameConsoleDisDTO) {
        final GameConsoleDis gameConsoleDis = gameConsoleDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gameConsoleDisDTO, gameConsoleDis);
        gameConsoleDisRepository.save(gameConsoleDis);
    }

    public void delete(final Long id) {
        final GameConsoleDis gameConsoleDis = gameConsoleDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteGameConsoleDis(id));
        gameConsoleDisRepository.delete(gameConsoleDis);
    }

    private GameConsoleDisDTO mapToDTO(final GameConsoleDis gameConsoleDis,
            final GameConsoleDisDTO gameConsoleDisDTO) {
        gameConsoleDisDTO.setId(gameConsoleDis.getId());
        return gameConsoleDisDTO;
    }

    private GameConsoleDis mapToEntity(final GameConsoleDisDTO gameConsoleDisDTO,
            final GameConsoleDis gameConsoleDis) {
        return gameConsoleDis;
    }

    public Map<Long, Long> getGameConsoleDisValues() {
        return gameConsoleDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GameConsoleDis::getId, GameConsoleDis::getId));
    }

}

