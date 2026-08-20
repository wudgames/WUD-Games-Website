package edu.wisc.wud.games.wud_games_website.board_game_expansion_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardGameExpansionDisDTO extends BoardGameDisDTO {
    @NotNull
    private BoardGameDis baseGame;
}