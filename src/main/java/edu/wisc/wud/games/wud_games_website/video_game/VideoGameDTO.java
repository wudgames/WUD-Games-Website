package edu.wisc.wud.games.wud_games_website.video_game;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VideoGameDTO {

    private Long id;

    @NotNull
    private Long videoGameDis;

}

