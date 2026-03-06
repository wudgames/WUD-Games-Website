package edu.wisc.wud.games.wud_games_website.checkout_record;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CheckoutRecordRepository extends JpaRepository<CheckoutRecord, Long> {

    List<CheckoutRecord> findAllByInventoryItemsId(Long id);

}

