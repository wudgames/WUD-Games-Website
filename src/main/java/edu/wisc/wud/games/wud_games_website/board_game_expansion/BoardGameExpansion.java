package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import edu.wisc.wud.games.wud_games_website.board_game.BoardGame;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BoardGameExpansion extends BoardGame {

}

