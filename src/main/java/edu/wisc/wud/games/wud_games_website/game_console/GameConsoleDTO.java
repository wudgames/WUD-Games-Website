package edu.wisc.wud.games.wud_games_website.game_console;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GameConsoleDTO {

    private Long id;

    @NotNull
    private Long gameConsoleDis;

}

