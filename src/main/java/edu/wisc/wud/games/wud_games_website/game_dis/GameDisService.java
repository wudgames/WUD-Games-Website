package edu.wisc.wud.games.wud_games_website.game_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GameDisService {

    private final GameDisRepository gameDisRepository;

    public GameDisService(final GameDisRepository gameDisRepository) {
        this.gameDisRepository = gameDisRepository;
    }

    public List<GameDisDTO> findAll() {
        final List<GameDis> gameDises = gameDisRepository.findAll(Sort.by("id"));
        return gameDises.stream()
                .map(gameDis -> mapToDTO(gameDis, new GameDisDTO()))
                .toList();
    }

    public GameDisDTO get(final Long id) {
        return gameDisRepository.findById(id)
                .map(gameDis -> mapToDTO(gameDis, new GameDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final GameDisDTO gameDisDTO) {
        final GameDis gameDis = new GameDis();
        mapToEntity(gameDisDTO, gameDis);
        return gameDisRepository.save(gameDis).getId();
    }

    public void update(final Long id, final GameDisDTO gameDisDTO) {
        final GameDis gameDis = gameDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(gameDisDTO, gameDis);
        gameDisRepository.save(gameDis);
    }

    public void delete(final Long id) {
        final GameDis gameDis = gameDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        gameDisRepository.delete(gameDis);
    }

    private GameDisDTO mapToDTO(final GameDis gameDis, final GameDisDTO gameDisDTO) {
        gameDisDTO.setId(gameDis.getId());
        gameDisDTO.setMinPlayers(gameDis.getMinPlayers());
        gameDisDTO.setMaxPlayers(gameDis.getMaxPlayers());
        return gameDisDTO;
    }

    public GameDis mapToEntity(final GameDisDTO gameDisDTO, final GameDis gameDis) {
        gameDis.setMinPlayers(gameDisDTO.getMinPlayers());
        gameDis.setMaxPlayers(gameDisDTO.getMaxPlayers());
        return gameDis;
    }

}

