package edu.wisc.wud.games.wud_games_website.video_game;

import edu.wisc.wud.games.wud_games_website.digital_item.DigitalItem;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class VideoGame extends DigitalItem {

}

