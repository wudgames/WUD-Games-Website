package edu.wisc.wud.games.wud_games_website.game_expansion_dis;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GameExpansionDisDTO extends GeneralDisDTO {
    @NotNull
    private Long baseGameDis;
}

