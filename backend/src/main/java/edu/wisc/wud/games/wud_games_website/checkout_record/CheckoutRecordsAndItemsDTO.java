package edu.wisc.wud.games.wud_games_website.checkout_record;

import java.util.List;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CheckoutRecordsAndItemsDTO {
    private CheckoutRecordDTO record;
    private List<InventoryItemDTO> items;
}
