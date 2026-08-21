package edu.wisc.wud.games.wud_games_website.board_game;

import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardGameDTO extends GameDisDTO {
    @NotNull
    private Integer minPlaytime;

    @NotNull
    private Integer maxPlaytime;

    @NotNull
    private Long boardGameDis;

}

