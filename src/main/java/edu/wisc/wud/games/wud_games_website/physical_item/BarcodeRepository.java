package edu.wisc.wud.games.wud_games_website.physical_item;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
    
}
