package edu.wisc.wud.games.wud_games_website.game_expansion_dis;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GameExpansionDisDTO {

    private Long id;

    @NotNull
    private Long baseGameDis;

}

