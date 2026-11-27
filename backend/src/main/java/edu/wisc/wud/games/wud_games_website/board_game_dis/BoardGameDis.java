package edu.wisc.wud.games.wud_games_website.board_game_dis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edu.wisc.wud.games.wud_games_website.game_dis.GameDis;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BoardGameDis extends GameDis {
        @Column
        private Integer minPlaytime;

        @Column
        private Integer maxPlaytime;
}
