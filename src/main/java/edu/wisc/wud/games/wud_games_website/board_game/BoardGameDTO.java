package edu.wisc.wud.games.wud_games_website.board_game;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardGameDTO {

    private Long id;

    @NotNull
    private Integer minPlaytime;

    @NotNull
    private Integer maxPlaytime;

    @NotNull
    private Long boardGameDis;

}

