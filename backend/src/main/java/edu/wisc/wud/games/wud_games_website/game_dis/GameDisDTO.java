package edu.wisc.wud.games.wud_games_website.game_dis;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GameDisDTO extends GeneralDisDTO {

    private Integer minPlayers;
    private Integer maxPlayers;

}

