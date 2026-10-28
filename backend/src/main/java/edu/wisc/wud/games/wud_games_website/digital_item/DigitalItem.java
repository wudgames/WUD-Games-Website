package edu.wisc.wud.games.wud_games_website.digital_item;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDis;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class DigitalItem extends InventoryItem {
    @ManyToMany
    @JoinTable(
            name = "AccountCompatibilityByDigitalItem",
            joinColumns = @JoinColumn(name = "accountDis_id"),
            inverseJoinColumns = @JoinColumn(name = "digitalItem_id")
    )
    private Set<AccountDis> compatibleAccounts = new HashSet<>();
}

