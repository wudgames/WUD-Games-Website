package edu.wisc.wud.games.wud_games_website.physical_item;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.location.LocationDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PhysicalItemDTO extends InventoryItemDTO {
    private LocationDTO location;
    private Long barcode;
}

