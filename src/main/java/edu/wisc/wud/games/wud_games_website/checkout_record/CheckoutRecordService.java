package edu.wisc.wud.games.wud_games_website.checkout_record;

import edu.wisc.wud.games.wud_games_website.events.before_create.BeforeCreateCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.events.brfore_update.BeforeUpdateCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

@Service
@Transactional(rollbackFor = Exception.class)
public class CheckoutRecordService extends EntityService<CheckoutRecordRepository, CheckoutRecord, CheckoutRecordDTO> {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;

    public CheckoutRecordService(CheckoutRecordRepository repository,
            CheckoutRecordMapper mapper, ApplicationEventPublisher publisher,
            final InventoryItemRepository inventoryItemRepository, final InventoryItemMapper inventoryItemMapper) {
        super(repository, mapper, publisher);
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemMapper = inventoryItemMapper;
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

    // Called when the update checkout form is submitted
    public ModelAndView updateCheckout(CheckoutRecordDTO checkoutRecord, Stream<Long> itemIds, Boolean markReturned) {
        CheckoutRecordDTO dto;
        if (checkoutRecord.getId() != null) {
            dto = get(checkoutRecord.getId());
            dto.setPeoplePlaying(checkoutRecord.getPeoplePlaying());
            dto.setRecipientName(checkoutRecord.getRecipientName());
        } else {
            dto = checkoutRecord;
        }
        if (itemIds != null) {
            dto.setInventoryItems(itemIds
                    .map(itemId -> inventoryItemMapper.toDTO(inventoryItemRepository.findById(itemId).orElseThrow()))
                    .collect(Collectors.toSet()));
        }
        if (dto.getCheckoutTime() == null) {
            dto.setCheckoutTime(OffsetDateTime.now());
        }
        // returned time
        if (markReturned) {
            dto.setReturnedTime(OffsetDateTime.now());
        } else {
            dto.setReturnedTime(null);
        }
        if (dto.getId() == null) {
            create(dto);
        } else {
            update(dto.getId(), dto);
        }
        return new ModelAndView("redirect:/library");// TODO change to hosting page
    }

    public ModelAndView markReturned(Long checkout_id) {
        CheckoutRecord checkoutRecord = repository.findById(checkout_id).orElseThrow();
        if (checkoutRecord.getReturnedTime() != null) {
            throw new IllegalStateException("Checkout is already returned");
        }
        checkoutRecord.setReturnedTime(OffsetDateTime.now());
        return new ModelAndView("redirect:library");
    }

    public CheckoutRecordDTO getActiveCheckoutFor(Long item_id) {
        return mapper.toDTO(repository.getActiveCheckoutFor(item_id));
    }

    @EventListener(BeforeDeleteInventoryItem.class)
    public void on(final BeforeDeleteInventoryItem event) {
        // remove many-to-many relations at owning side
        repository.findAllByInventoryItemsId(event.getId()).forEach(checkoutRecord -> checkoutRecord.getInventoryItems()
                .removeIf(inventoryItem -> inventoryItem.getId().equals(event.getId())));
    }

    private void validateUpdatedCheckoutRecord(CheckoutRecordDTO checkoutRecord) {
        System.out.println("Validating update to checkout record: " + checkoutRecord.getId());
        // Check if the checkout record is current
        if (checkoutRecord.getReturnedTime() != null) {
            return;
        }
        Long updatedRecordId = checkoutRecord.getId();
        // Confirm that no items are already checkout on a different checkout record
        Set<InventoryItemDTO> inventoryItems = checkoutRecord.getInventoryItems();
        for (InventoryItemDTO item : inventoryItems) {
            System.out.println("Checking current status of item " + item.getId());
            CheckoutRecordDTO currentCheckoutRecord = getActiveCheckoutFor(item.getId());
            if (currentCheckoutRecord != null) {
                if (updatedRecordId != null) {
                    // record being validated is an update to existing record
                    if (currentCheckoutRecord.getId() != updatedRecordId) {
                        System.out.println("    Item is currently checked out by a different record.");
                        throw new RuntimeException("Cannot update checkout record " + updatedRecordId
                                + ", because item "
                                + item.getId() + " is already checked out by record " + currentCheckoutRecord.getId());
                    } else {
                        System.out.println("    Item is currently checked out by this record.");
                    }
                } else {
                    // record being validated is new
                    System.out.println("    Item is currently checked out by a different record.");
                    throw new RuntimeException("Cannot create new checkout record, because item "
                            + item.getId() + " is already checked out by record " + currentCheckoutRecord.getId());
                }
            } else {
                System.out.println("    Item is currently not checked out");
            }
        }
    }

    @EventListener(BeforeCreateCheckoutRecord.class)
    public void onBeforeCreateCheckoutRecord(final BeforeCreateCheckoutRecord event) {
        validateUpdatedCheckoutRecord(event.getRecordBeingCreated());
    }

    @EventListener(BeforeUpdateCheckoutRecord.class)
    public void onBeforeUpdateCheckoutRecord(final BeforeUpdateCheckoutRecord event) {
        Long checkoutRecordId = event.getId();
        validateUpdatedCheckoutRecord(get(checkoutRecordId));
    }

}
