package edu.wisc.wud.games.wud_games_website.checkout_record;

import edu.wisc.wud.games.wud_games_website.events.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class CheckoutRecordService extends EntityService<CheckoutRecordRepository, CheckoutRecord, CheckoutRecordDTO> {

    public CheckoutRecordService(CheckoutRecordRepository repository,
            CheckoutRecordMapper mapper, ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected CheckoutRecord newEntity() {
        return new CheckoutRecord();
    }

    @Override
    public CheckoutRecordDTO newDTO() {
        return new CheckoutRecordDTO();
    }

    public Map<Long, Long> getCheckoutRecordValues() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(CheckoutRecord::getId, CheckoutRecord::getId));
    }

    @EventListener(BeforeDeleteInventoryItem.class)
    public void on(final BeforeDeleteInventoryItem event) {
        // remove many-to-many relations at owning side
        repository.findAllByInventoryItemsId(event.getId()).forEach(checkoutRecord ->
                checkoutRecord.getInventoryItems().removeIf(inventoryItem -> inventoryItem.getId().equals(event.getId())));
    }
}

