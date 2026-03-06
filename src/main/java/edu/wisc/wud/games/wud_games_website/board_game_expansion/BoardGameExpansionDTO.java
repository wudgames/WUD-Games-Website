package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardGameExpansionDTO {

    private Long id;

    @NotNull
    private Long gameExpansionDis;

}

