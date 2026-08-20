package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import edu.wisc.wud.games.wud_games_website.board_game.BoardGameDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardGameExpansionDTO extends BoardGameDTO {
    @NotNull
    private Long gameExpansionDis;

}

