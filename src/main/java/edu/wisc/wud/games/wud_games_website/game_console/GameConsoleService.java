package edu.wisc.wud.games.wud_games_website.game_console;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameConsoleDis;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDis;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisRepository;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GameConsoleService {

    private final GameConsoleRepository gameConsoleRepository;
    private final GameConsoleDisRepository gameConsoleDisRepository;

    public GameConsoleService(final GameConsoleRepository gameConsoleRepository,
            final GameConsoleDisRepository gameConsoleDisRepository) {
        this.gameConsoleRepository = gameConsoleRepository;
        this.gameConsoleDisRepository = gameConsoleDisRepository;
    }

    public List<GameConsoleDTO> findAll() {
        final List<GameConsole> gameConsoles = gameConsoleRepository.findAll(Sort.by("id"));
        return gameConsoles.stream()
                .map(gameConsole -> mapToDTO(gameConsole, new GameConsoleDTO()))
                .toList();
    }

    public GameConsoleDTO get(final Long id) {
        return gameConsoleRepository.findById(id)
                .map(gameConsole -> mapToDTO(gameConsole, new GameConsoleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GameConsoleDTO gameConsoleDTO) {
        final GameConsole gameConsole = new GameConsole();
        mapToEntity(gameConsoleDTO, gameConsole);
        return gameConsoleRepository.save(gameConsole).getId();
    }

    public void update(final Long id, final GameConsoleDTO gameConsoleDTO) {
        final GameConsole gameConsole = gameConsoleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gameConsoleDTO, gameConsole);
        gameConsoleRepository.save(gameConsole);
    }

    public void delete(final Long id) {
        final GameConsole gameConsole = gameConsoleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        gameConsoleRepository.delete(gameConsole);
    }

    private GameConsoleDTO mapToDTO(final GameConsole gameConsole,
            final GameConsoleDTO gameConsoleDTO) {
        gameConsoleDTO.setId(gameConsole.getId());
        gameConsoleDTO.setGameConsoleDis(gameConsole.getGameConsoleDis() == null ? null : gameConsole.getGameConsoleDis().getId());
        return gameConsoleDTO;
    }

    private GameConsole mapToEntity(final GameConsoleDTO gameConsoleDTO,
            final GameConsole gameConsole) {
        final GameConsoleDis gameConsoleDis = gameConsoleDTO.getGameConsoleDis() == null ? null : gameConsoleDisRepository.findById(gameConsoleDTO.getGameConsoleDis())
                .orElseThrow(() -> new NotFoundException("gameConsoleDis not found"));
        gameConsole.setGameConsoleDis(gameConsoleDis);
        return gameConsole;
    }

    @EventListener(BeforeDeleteGameConsoleDis.class)
    public void on(final BeforeDeleteGameConsoleDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final GameConsole gameConsoleDisGameConsole = gameConsoleRepository.findFirstByGameConsoleDisId(event.getId());
        if (gameConsoleDisGameConsole != null) {
            referencedException.setKey("gameConsoleDis.gameConsole.gameConsoleDis.referenced");
            referencedException.addParam(gameConsoleDisGameConsole.getId());
            throw referencedException;
        }
    }

}

