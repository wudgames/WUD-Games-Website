package edu.wisc.wud.games.wud_games_website.inventory_item;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecord;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryItemService extends EntityService<InventoryItemRepository, InventoryItem, InventoryItemDTO> {

    private final InventoryItemRepository inventoryItemRepository;
    private final CheckoutRecordRepository checkoutRecordRepository;

    public InventoryItemService(final InventoryItemRepository inventoryItemRepository,
            final InventoryItemMapper mapper,
            final CheckoutRecordRepository checkoutRecordRepository,
            final ApplicationEventPublisher publisher) {
        super(inventoryItemRepository, mapper, publisher);
        this.inventoryItemRepository = inventoryItemRepository;
        this.checkoutRecordRepository = checkoutRecordRepository;
    }

    @Override
    protected InventoryItem newEntity() {
        return new InventoryItem();
    }

    @Override
    public InventoryItemDTO newDTO() {
        return new InventoryItemDTO();
    }

    public Map<Long, Long> getInventoryItemValues() {
        return inventoryItemRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(InventoryItem::getId, InventoryItem::getId));
    }

    class GeneralDisListener {
        // Stop a description of an item from being deleted if there are still items using that description
        @EventListener(BeforeDelete.class)
        public void on(final BeforeDelete<GeneralDis> event) {
            final ReferencedException referencedException = new ReferencedException();
            final InventoryItem genDisInventoryItem = inventoryItemRepository.findFirstByGenDisId(event.getId());
            if (genDisInventoryItem != null) {
                referencedException.setKey("generalDis.inventoryItem.genDis.referenced");
                referencedException.addParam(genDisInventoryItem.getId());
                throw referencedException;
            }
        }
    }

    class CheckoutRecordListener {
        // Stop a checkout record from being deleted if there are still items using that checkout record
        @EventListener(BeforeDelete.class)
        public void on(final BeforeDelete<CheckoutRecord> event) {
            final ReferencedException referencedException = new ReferencedException();
            // TODO update check for current fields
            final CheckoutRecord recordToBeDeleted = checkoutRecordRepository.getReferenceById(event.getId());
            if (recordToBeDeleted.getReturnedTime() == null && recordToBeDeleted.getInventoryItems() != null && recordToBeDeleted.getInventoryItems().size() > 0) {
                // This item is currently checkout to the that record
                referencedException.setKey("checkoutRecord.inventoryItem.currentCheckout.referenced");
                referencedException.addParam(recordToBeDeleted.getInventoryItems().get(0).getId());
                throw referencedException;
            }
        }
    }    
}

