package edu.wisc.wud.games.wud_games_website.checkout_record;

import edu.wisc.wud.games.wud_games_website.events.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;

import java.time.OffsetDateTime;
import java.util.Map;
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

    private CheckoutRecordDTO parseDTO(Long checkoutId, Stream<Long> itemIds, Integer peoplePlaying, String recipientName) {
        CheckoutRecordDTO dto;
        if (checkoutId != null) {
            dto = get(checkoutId);
        } else {
            dto = new CheckoutRecordDTO();
        }
        // what items were checked out
        dto.setInventoryItems(itemIds.map(itemId -> inventoryItemMapper.toDTO(inventoryItemRepository.findById(itemId).orElseThrow()))
                .collect(Collectors.toSet()));
        // number of people playing
        dto.setPeoplePlaying(peoplePlaying);
        // who checked it out
        dto.setRecipientName(recipientName);
        return dto;
    }

    // Called when the update checkout form is submitted
    public ModelAndView updateCheckout(Long checkoutId, Stream<Long> itemIds, Integer peoplePlaying, String recipientName, Boolean markReturned) {
        CheckoutRecordDTO dto = parseDTO(checkoutId, itemIds, peoplePlaying, recipientName);
        if (dto.getCheckoutTime() == null) {
            dto.setCheckoutTime(OffsetDateTime.now());
        }
        // returned time
        if (markReturned) {
            dto.setReturnedTime(OffsetDateTime.now());
        } else {
            dto.setReturnedTime(null);
        }
        repository.save(mapper.toEntity(dto));
        return new ModelAndView("library");// TODO change to hosting page
    }

    @EventListener(BeforeDeleteInventoryItem.class)
    public void on(final BeforeDeleteInventoryItem event) {
        // remove many-to-many relations at owning side
        repository.findAllByInventoryItemsId(event.getId()).forEach(checkoutRecord -> checkoutRecord.getInventoryItems()
                .removeIf(inventoryItem -> inventoryItem.getId().equals(event.getId())));
    }
}
