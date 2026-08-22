package edu.wisc.wud.games.wud_games_website.game_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;

@Component
public class GameDisMapper extends EntityMapper<GameDis, GameDisDTO> {
    public GameDisMapper(GeneralDisMapper generalDisMapper) {
        super(generalDisMapper, GameDis.class);
    }

    public GameDisDTO localToDTO(final GameDis gameDis,
            final GameDisDTO gameDisDTO) {
        gameDisDTO.setMinPlayers(gameDis.getMinPlayers());
        gameDisDTO.setMaxPlayers(gameDis.getMaxPlayers());
        return gameDisDTO;
    }

    public GameDis localToEntity(final GameDisDTO gameDisDTO,
            final GameDis gameDis) {
        gameDis.setMinPlayers(gameDisDTO.getMinPlayers());
        gameDis.setMaxPlayers(gameDisDTO.getMaxPlayers());
        return gameDis;
    }
}
