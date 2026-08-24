package edu.wisc.wud.games.wud_games_website.game_console;

import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItem;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GameConsole extends PhysicalItem {

}

