package edu.wisc.wud.games.wud_games_website.board_game_dis;

import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardGameDisDTO extends GameDisDTO {

    private Integer minPlaytime;
    private Integer maxPlaytime;

}

