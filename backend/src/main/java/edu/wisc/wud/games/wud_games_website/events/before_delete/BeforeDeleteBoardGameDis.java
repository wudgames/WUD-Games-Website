package edu.wisc.wud.games.wud_games_website.events.before_delete;

import edu.wisc.wud.games.wud_games_website.game_dis.GameDis;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public class BeforeDeleteBoardGameDis extends BeforeDeleteGameDis {

    public BeforeDeleteBoardGameDis(Long id) {
        super(id);
        //TODO Auto-generated constructor stub
    }

}

