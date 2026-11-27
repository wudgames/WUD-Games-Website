package edu.wisc.wud.games.wud_games_website.checkout_record;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;


public interface CheckoutRecordRepository extends JpaRepository<CheckoutRecord, Long> {

    Set<CheckoutRecord> findAllByInventoryItemsId(Long id);

    //@Query(value = "SELECT cr FROM CheckoutRecord cr INNER JOIN ItemsByCheckoutRecord ON CheckoutRecord.id=ItemsByCheckoutRecord.checkoutRecordId WHERE ItemsByCheckoutRecord.inventoryItemId = :item_id AND cr.returnedTime IS null")
    @NativeQuery(value = "SELECT * FROM checkout_record " + //
                "INNER JOIN items_by_checkout_record " + //
                "ON checkout_record.id=items_by_checkout_record.checkout_record_id " + //
                "WHERE returned_time IS NULL AND items_by_checkout_record.inventory_item_id = :item_id")
    CheckoutRecord getActiveCheckoutFor(Long item_id);
}

