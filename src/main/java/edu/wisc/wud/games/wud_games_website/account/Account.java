package edu.wisc.wud.games.wud_games_website.account;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Account extends InventoryItem {

}

