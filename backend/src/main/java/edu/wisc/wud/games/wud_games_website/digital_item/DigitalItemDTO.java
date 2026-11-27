package edu.wisc.wud.games.wud_games_website.digital_item;

import java.util.Set;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDisDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DigitalItemDTO extends InventoryItemDTO {
    @OneToMany
    private Set<AccountDisDTO> compatibleAccounts;
}

