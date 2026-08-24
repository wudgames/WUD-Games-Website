package edu.wisc.wud.games.wud_games_website.inventory_item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;


public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    InventoryItem findFirstByGenDisId(Long id);
    List<InventoryItem> findByGenDis(GeneralDis genDis);

}

