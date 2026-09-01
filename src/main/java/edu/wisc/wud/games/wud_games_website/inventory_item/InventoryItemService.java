package edu.wisc.wud.games.wud_games_website.inventory_item;

import edu.wisc.wud.games.wud_games_website.board_game.BoardGameDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion.BoardGameExpansionDTO;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDisDTO;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecord;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordMapper;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordRepository;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordService;
import edu.wisc.wud.games.wud_games_website.digital_item.DigitalItemDTO;
import edu.wisc.wud.games.wud_games_website.equipment.EquipmentDTO;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisDTO;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.game_console.GameConsoleDTO;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDisDTO;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.location.LocationDTO;
import edu.wisc.wud.games.wud_games_website.location.LocationMapper;
import edu.wisc.wud.games.wud_games_website.location.LocationRepository;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemDTO;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import edu.wisc.wud.games.wud_games_website.video_game.VideoGameDTO;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDisDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
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
public class InventoryItemService extends EntityService<InventoryItemRepository, InventoryItem, InventoryItemDTO> {

    private final InventoryItemRepository inventoryItemRepository;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final GeneralDisService generalDisService;
    private final GeneralDisMapper generalDisMapper;
    private final CheckoutRecordRepository checkoutRecordRepository;
    private final CheckoutRecordMapper checkoutRecordMapper;

    private final Map<Class<? extends GeneralDisDTO>, Supplier<InventoryItemDTO>> generalDisDTOToInventoryItemDTOMap = new HashMap<>();

    public InventoryItemService(final InventoryItemRepository inventoryItemRepository,
            final InventoryItemMapper mapper,
            final CheckoutRecordRepository checkoutRecordRepository,
            final ApplicationEventPublisher publisher, final GeneralDisService generalDisService,
            final GeneralDisMapper generalDisMapper, final LocationRepository locationRepository,
            final LocationMapper locationMapper, final CheckoutRecordMapper checkoutRecordMapper) {
        super(inventoryItemRepository, mapper, publisher);
        this.generalDisService = generalDisService;
        this.generalDisMapper = generalDisMapper;
        this.inventoryItemRepository = inventoryItemRepository;
        this.checkoutRecordRepository = checkoutRecordRepository;
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.checkoutRecordMapper = checkoutRecordMapper;

        // generalDisDTOToInventoryItemDTOMap.put(GeneralDisDTO.class, () -> new
        // InventoryItemDTO());
        generalDisDTOToInventoryItemDTOMap.put(BoardGameDisDTO.class, () -> new BoardGameDTO());
        generalDisDTOToInventoryItemDTOMap.put(BoardGameExpansionDisDTO.class, () -> new BoardGameExpansionDTO());
        generalDisDTOToInventoryItemDTOMap.put(VideoGameDisDTO.class, () -> new VideoGameDTO());
        // Add video game expansion here
        generalDisDTOToInventoryItemDTOMap.put(EquipmentDisDTO.class, () -> new EquipmentDTO());
        generalDisDTOToInventoryItemDTOMap.put(GameConsoleDisDTO.class, () -> new GameConsoleDTO());
    }

    @Override
    protected InventoryItem newEntity() {
        return new InventoryItem();
    }

    @Override
    public InventoryItemDTO newDTO() {
        return new InventoryItemDTO();
    }

    public List<InventoryItemDTO> findByGenDis(GeneralDisDTO description) {
        return mapper.allToDTO(repository.findByGenDis(generalDisMapper.toEntity(description)));
    }

    public ModelAndView getInventoryItemsFor(Long description_id) {
        GeneralDis generalDis = generalDisMapper.toEntity(generalDisService.get(description_id));
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByGenDis(generalDis);
        List<ItemRowDTO> rowDTOs = inventoryItems.stream().map(entity -> {
            ItemRowDTO row = new ItemRowDTO();
            row.setItem(mapper.toDTO(entity));
            // TODO set current checkout record
            row.setCheckoutRecord(checkoutRecordMapper.toDTO(checkoutRecordRepository.getActiveCheckoutFor(entity.getId())));
            return row;
        }).toList();
        ModelAndView model = new ModelAndView("search/itemsTable");
        model.addObject("rowDTOs", rowDTOs);
        return model;
    }

    public void createItemFor(Long description_id, HttpServletRequest request) {
        final GeneralDisDTO generalDisDTO = generalDisService.get(description_id);
        Supplier<InventoryItemDTO> inventoryItemDTOSupplier = generalDisDTOToInventoryItemDTOMap
                .get(generalDisDTO.getClass());
        if (inventoryItemDTOSupplier == null) {
            throw new UnsupportedOperationException(
                    "InventoryItemService does not have a item supplier for " + generalDisDTO.getClass());
        }
        InventoryItemDTO inventoryItemDTO = generalDisDTOToInventoryItemDTOMap.get(generalDisDTO.getClass()).get();
        if (inventoryItemDTO instanceof PhysicalItemDTO) {
            if (!request.isUserInRole("PHYSICAL_INVENTORY_MANAGER")) {
                throw new InvalidParameterException("You do not have permission to create physical inventory items");
            }
        } else if (inventoryItemDTO instanceof DigitalItemDTO) {
            if (!request.isUserInRole("DIGITAL_INVENTORY_MANAGER")) {
                throw new InvalidParameterException("You do not have permission to create digital inventory items");
            }
        } else {
            throw new UnsupportedOperationException(
                    "Authorization could not be confirmed for type " + inventoryItemDTO.getClass());
        }
        inventoryItemDTO.setGenDis(generalDisDTO);
        if (inventoryItemDTO instanceof PhysicalItemDTO) {
            LocationDTO unknownLocation = locationMapper.toDTO(locationRepository.findByName("Unknown").orElseThrow());
            ((PhysicalItemDTO) inventoryItemDTO).setLocation(unknownLocation);
        }
        this.create(inventoryItemDTO);
    }

    public Map<Long, Long> getInventoryItemValues() {
        return inventoryItemRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(InventoryItem::getId, InventoryItem::getId));
    }

    public Set<InventoryItemDTO> findAllById(Stream<Long> ids) {
        return ids.map(id -> get(id)).collect(Collectors.toSet());
    }

    class GeneralDisListener {
        // Stop a description of an item from being deleted if there are still items
        // using that description
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

    class CheckoutRecordListener {
        // Stop a checkout record from being deleted if there are still items using that
        // checkout record
        @EventListener(BeforeDeleteCheckoutRecord.class)
        public void on(final BeforeDeleteCheckoutRecord event) {
            final ReferencedException referencedException = new ReferencedException();
            // TODO update check for current fields
            final CheckoutRecord recordToBeDeleted = checkoutRecordRepository.getReferenceById(event.getId());
            if (recordToBeDeleted.getReturnedTime() == null && recordToBeDeleted.getInventoryItems() != null
                    && recordToBeDeleted.getInventoryItems().size() > 0) {
                // This item is currently checkout to the that record
                referencedException.setKey("checkoutRecord.inventoryItem.currentCheckout.referenced");
                referencedException.addParam(recordToBeDeleted.getInventoryItems().iterator().next().getId());
                throw referencedException;
            }
        }
    }
}
