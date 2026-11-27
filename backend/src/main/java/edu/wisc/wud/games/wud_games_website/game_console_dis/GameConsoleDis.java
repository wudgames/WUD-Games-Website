package edu.wisc.wud.games.wud_games_website.game_console_dis;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GameConsoleDis extends GeneralDis {

}

