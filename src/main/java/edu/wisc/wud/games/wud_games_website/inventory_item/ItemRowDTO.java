package edu.wisc.wud.games.wud_games_website.inventory_item;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRowDTO {
    private InventoryItemDTO item;
    private CheckoutRecordDTO checkoutRecord;
}
