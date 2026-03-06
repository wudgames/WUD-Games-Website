package edu.wisc.wud.games.wud_games_website.inventory_item;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final GeneralDisRepository generalDisRepository;
    private final ApplicationEventPublisher publisher;

    public InventoryItemService(final InventoryItemRepository inventoryItemRepository,
            final GeneralDisRepository generalDisRepository,
            final ApplicationEventPublisher publisher) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.generalDisRepository = generalDisRepository;
        this.publisher = publisher;
    }

    public List<InventoryItemDTO> findAll() {
        final List<InventoryItem> inventoryItems = inventoryItemRepository.findAll(Sort.by("id"));
        return inventoryItems.stream()
                .map(inventoryItem -> mapToDTO(inventoryItem, new InventoryItemDTO()))
                .toList();
    }

    public InventoryItemDTO get(final Long id) {
        return inventoryItemRepository.findById(id)
                .map(inventoryItem -> mapToDTO(inventoryItem, new InventoryItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final InventoryItemDTO inventoryItemDTO) {
        final InventoryItem inventoryItem = new InventoryItem();
        mapToEntity(inventoryItemDTO, inventoryItem);
        return inventoryItemRepository.save(inventoryItem).getId();
    }

    public void update(final Long id, final InventoryItemDTO inventoryItemDTO) {
        final InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(inventoryItemDTO, inventoryItem);
        inventoryItemRepository.save(inventoryItem);
    }

    public void delete(final Long id) {
        final InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteInventoryItem(id));
        inventoryItemRepository.delete(inventoryItem);
    }

    private InventoryItemDTO mapToDTO(final InventoryItem inventoryItem,
            final InventoryItemDTO inventoryItemDTO) {
        inventoryItemDTO.setId(inventoryItem.getId());
        inventoryItemDTO.setDateAdded(inventoryItem.getDateAdded());
        inventoryItemDTO.setNotes(inventoryItem.getNotes());
        inventoryItemDTO.setGenDis(inventoryItem.getGenDis() == null ? null : inventoryItem.getGenDis().getId());
        return inventoryItemDTO;
    }

    private InventoryItem mapToEntity(final InventoryItemDTO inventoryItemDTO,
            final InventoryItem inventoryItem) {
        inventoryItem.setDateAdded(inventoryItemDTO.getDateAdded());
        inventoryItem.setNotes(inventoryItemDTO.getNotes());
        final GeneralDis genDis = inventoryItemDTO.getGenDis() == null ? null : generalDisRepository.findById(inventoryItemDTO.getGenDis())
                .orElseThrow(() -> new NotFoundException("genDis not found"));
        inventoryItem.setGenDis(genDis);
        return inventoryItem;
    }

    public Map<Long, Long> getInventoryItemValues() {
        return inventoryItemRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(InventoryItem::getId, InventoryItem::getId));
    }

    @EventListener(BeforeDeleteGeneralDis.class)
    public void on(final BeforeDeleteGeneralDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final InventoryItem genDisInventoryItem = inventoryItemRepository.findFirstByGenDisId(event.getId());
        if (genDisInventoryItem != null) {
            referencedException.setKey("generalDis.inventoryItem.genDis.referenced");
            referencedException.addParam(genDisInventoryItem.getId());
            throw referencedException;
        }
    }

}

