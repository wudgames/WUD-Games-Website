package edu.wisc.wud.games.wud_games_website.inventory_item;

import org.springframework.stereotype.Component;

@Component
public class InventoryItemSubclassMapper {
    public InventoryItemSubclassMapper() {
        
    }

    public InventoryItemDTO toDTO(final InventoryItem entity) {
        throw new UnsupportedOperationException("This method has not been implemented.");
    }

    public InventoryItem toEntity(final InventoryItemDTO dto) {
        throw new UnsupportedOperationException("This method has not been implemented.");
    }
}
