package edu.wisc.wud.games.wud_games_website.checkout_record;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class CheckoutRecordService {

    private final CheckoutRecordRepository checkoutRecordRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ApplicationEventPublisher publisher;

    public CheckoutRecordService(final CheckoutRecordRepository checkoutRecordRepository,
            final InventoryItemRepository inventoryItemRepository,
            final ApplicationEventPublisher publisher) {
        this.checkoutRecordRepository = checkoutRecordRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.publisher = publisher;
    }

    public List<CheckoutRecordDTO> findAll() {
        final List<CheckoutRecord> checkoutRecords = checkoutRecordRepository.findAll(Sort.by("id"));
        return checkoutRecords.stream()
                .map(checkoutRecord -> mapToDTO(checkoutRecord, new CheckoutRecordDTO()))
                .toList();
    }

    public CheckoutRecordDTO get(final Long id) {
        return checkoutRecordRepository.findById(id)
                .map(checkoutRecord -> mapToDTO(checkoutRecord, new CheckoutRecordDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CheckoutRecordDTO checkoutRecordDTO) {
        final CheckoutRecord checkoutRecord = new CheckoutRecord();
        mapToEntity(checkoutRecordDTO, checkoutRecord);
        return checkoutRecordRepository.save(checkoutRecord).getId();
    }

    public void update(final Long id, final CheckoutRecordDTO checkoutRecordDTO) {
        final CheckoutRecord checkoutRecord = checkoutRecordRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(checkoutRecordDTO, checkoutRecord);
        checkoutRecordRepository.save(checkoutRecord);
    }

    public void delete(final Long id) {
        final CheckoutRecord checkoutRecord = checkoutRecordRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCheckoutRecord(id));
        checkoutRecordRepository.delete(checkoutRecord);
    }

    private CheckoutRecordDTO mapToDTO(final CheckoutRecord checkoutRecord,
            final CheckoutRecordDTO checkoutRecordDTO) {
        checkoutRecordDTO.setId(checkoutRecord.getId());
        checkoutRecordDTO.setCheckoutTime(checkoutRecord.getCheckoutTime());
        checkoutRecordDTO.setReturnedTime(checkoutRecord.getReturnedTime());
        checkoutRecordDTO.setPeoplePlaying(checkoutRecord.getPeoplePlaying());
        checkoutRecordDTO.setResipeantName(checkoutRecord.getResipeantName());
        checkoutRecordDTO.setInventoryItems(checkoutRecord.getInventoryItems().stream()
                .map(inventoryItem -> inventoryItem.getId())
                .toList());
        return checkoutRecordDTO;
    }

    private CheckoutRecord mapToEntity(final CheckoutRecordDTO checkoutRecordDTO,
            final CheckoutRecord checkoutRecord) {
        checkoutRecord.setCheckoutTime(checkoutRecordDTO.getCheckoutTime());
        checkoutRecord.setReturnedTime(checkoutRecordDTO.getReturnedTime());
        checkoutRecord.setPeoplePlaying(checkoutRecordDTO.getPeoplePlaying());
        checkoutRecord.setResipeantName(checkoutRecordDTO.getResipeantName());
        final List<InventoryItem> inventoryItems = inventoryItemRepository.findAllById(
                checkoutRecordDTO.getInventoryItems() == null ? List.of() : checkoutRecordDTO.getInventoryItems());
        if (inventoryItems.size() != (checkoutRecordDTO.getInventoryItems() == null ? 0 : checkoutRecordDTO.getInventoryItems().size())) {
            throw new NotFoundException("one of inventoryItems not found");
        }
        checkoutRecord.setInventoryItems(new HashSet<>(inventoryItems));
        return checkoutRecord;
    }

    public Map<Long, Long> getCheckoutRecordValues() {
        return checkoutRecordRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(CheckoutRecord::getId, CheckoutRecord::getId));
    }

    @EventListener(BeforeDeleteInventoryItem.class)
    public void on(final BeforeDeleteInventoryItem event) {
        // remove many-to-many relations at owning side
        checkoutRecordRepository.findAllByInventoryItemsId(event.getId()).forEach(checkoutRecord ->
                checkoutRecord.getInventoryItems().removeIf(inventoryItem -> inventoryItem.getId().equals(event.getId())));
    }

}

