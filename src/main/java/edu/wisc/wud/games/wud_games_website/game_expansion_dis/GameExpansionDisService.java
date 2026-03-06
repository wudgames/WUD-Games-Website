package edu.wisc.wud.games.wud_games_website.game_expansion_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GameExpansionDisService {

    private final GameExpansionDisRepository gameExpansionDisRepository;
    private final BoardGameDisRepository boardGameDisRepository;
    private final ApplicationEventPublisher publisher;

    public GameExpansionDisService(final GameExpansionDisRepository gameExpansionDisRepository,
            final BoardGameDisRepository boardGameDisRepository,
            final ApplicationEventPublisher publisher) {
        this.gameExpansionDisRepository = gameExpansionDisRepository;
        this.boardGameDisRepository = boardGameDisRepository;
        this.publisher = publisher;
    }

    public List<GameExpansionDisDTO> findAll() {
        final List<GameExpansionDis> gameExpansionDises = gameExpansionDisRepository.findAll(Sort.by("id"));
        return gameExpansionDises.stream()
                .map(gameExpansionDis -> mapToDTO(gameExpansionDis, new GameExpansionDisDTO()))
                .toList();
    }

    public GameExpansionDisDTO get(final Long id) {
        return gameExpansionDisRepository.findById(id)
                .map(gameExpansionDis -> mapToDTO(gameExpansionDis, new GameExpansionDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GameExpansionDisDTO gameExpansionDisDTO) {
        final GameExpansionDis gameExpansionDis = new GameExpansionDis();
        mapToEntity(gameExpansionDisDTO, gameExpansionDis);
        return gameExpansionDisRepository.save(gameExpansionDis).getId();
    }

    public void update(final Long id, final GameExpansionDisDTO gameExpansionDisDTO) {
        final GameExpansionDis gameExpansionDis = gameExpansionDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gameExpansionDisDTO, gameExpansionDis);
        gameExpansionDisRepository.save(gameExpansionDis);
    }

    public void delete(final Long id) {
        final GameExpansionDis gameExpansionDis = gameExpansionDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteGameExpansionDis(id));
        gameExpansionDisRepository.delete(gameExpansionDis);
    }

    private GameExpansionDisDTO mapToDTO(final GameExpansionDis gameExpansionDis,
            final GameExpansionDisDTO gameExpansionDisDTO) {
        gameExpansionDisDTO.setId(gameExpansionDis.getId());
        gameExpansionDisDTO.setBaseGameDis(gameExpansionDis.getBaseGameDis() == null ? null : gameExpansionDis.getBaseGameDis().getId());
        return gameExpansionDisDTO;
    }

    private GameExpansionDis mapToEntity(final GameExpansionDisDTO gameExpansionDisDTO,
            final GameExpansionDis gameExpansionDis) {
        final BoardGameDis baseGameDis = gameExpansionDisDTO.getBaseGameDis() == null ? null : boardGameDisRepository.findById(gameExpansionDisDTO.getBaseGameDis())
                .orElseThrow(() -> new NotFoundException("baseGameDis not found"));
        gameExpansionDis.setBaseGameDis(baseGameDis);
        return gameExpansionDis;
    }

    public Map<Long, Long> getGameExpansionDisValues() {
        return gameExpansionDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GameExpansionDis::getId, GameExpansionDis::getId));
    }

    @EventListener(BeforeDeleteBoardGameDis.class)
    public void on(final BeforeDeleteBoardGameDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final GameExpansionDis baseGameDisGameExpansionDis = gameExpansionDisRepository.findFirstByBaseGameDisId(event.getId());
        if (baseGameDisGameExpansionDis != null) {
            referencedException.setKey("boardGameDis.gameExpansionDis.baseGameDis.referenced");
            referencedException.addParam(baseGameDisGameExpansionDis.getId());
            throw referencedException;
        }
    }

}

