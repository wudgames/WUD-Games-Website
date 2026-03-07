package edu.wisc.wud.games.wud_games_website.inventory_item;

import org.springframework.data.jpa.repository.JpaRepository;


public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    InventoryItem findFirstByGenDisId(Long id);

    InventoryItem findFirstByCurrentCheckoutId(Long id);

}

