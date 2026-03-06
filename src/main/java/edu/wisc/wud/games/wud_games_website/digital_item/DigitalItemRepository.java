package edu.wisc.wud.games.wud_games_website.digital_item;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DigitalItemRepository extends JpaRepository<DigitalItem, Long> {

    List<DigitalItem> findAllByCompatableAccountsId(Long id);

}

