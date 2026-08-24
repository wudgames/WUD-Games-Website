package edu.wisc.wud.games.wud_games_website.video_game_dis;

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
public class VideoGameDis extends GameDis {

}

