package edu.wisc.wud.games.wud_games_website.physical_item;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PhysicalItemRepository extends JpaRepository<PhysicalItem, Long> {

    PhysicalItem findFirstByLocationId(Long id);

}

