package edu.wisc.wud.games.wud_games_website.general_dis;

import java.util.List;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenDisWithAvailabilityDTO {
    private GeneralDisDTO description;
    private List<InventoryItemDTO> allItems;
    private List<InventoryItemDTO> checkedOutItems;
}
