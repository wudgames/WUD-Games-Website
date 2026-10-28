package edu.wisc.wud.games.wud_games_website.rental_request;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecord;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordRepository;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.HashSet;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class RentalRequestService {

    private final RentalRequestRepository rentalRequestRepository;
    private final CheckoutRecordRepository checkoutRecordRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public RentalRequestService(final RentalRequestRepository rentalRequestRepository,
            final CheckoutRecordRepository checkoutRecordRepository,
            final InventoryItemRepository inventoryItemRepository) {
        this.rentalRequestRepository = rentalRequestRepository;
        this.checkoutRecordRepository = checkoutRecordRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public List<RentalRequestDTO> findAll() {
        final List<RentalRequest> rentalRequests = rentalRequestRepository.findAll(Sort.by("id"));
        return rentalRequests.stream()
                .map(rentalRequest -> mapToDTO(rentalRequest, new RentalRequestDTO()))
                .toList();
    }

    public RentalRequestDTO get(final Long id) {
        return rentalRequestRepository.findById(id)
                .map(rentalRequest -> mapToDTO(rentalRequest, new RentalRequestDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RentalRequestDTO rentalRequestDTO) {
        final RentalRequest rentalRequest = new RentalRequest();
        mapToEntity(rentalRequestDTO, rentalRequest);
        return rentalRequestRepository.save(rentalRequest).getId();
    }

    public void update(final Long id, final RentalRequestDTO rentalRequestDTO) {
        final RentalRequest rentalRequest = rentalRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(rentalRequestDTO, rentalRequest);
        rentalRequestRepository.save(rentalRequest);
    }

    public void delete(final Long id) {
        final RentalRequest rentalRequest = rentalRequestRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        rentalRequestRepository.delete(rentalRequest);
    }

    private RentalRequestDTO mapToDTO(final RentalRequest rentalRequest,
            final RentalRequestDTO rentalRequestDTO) {
        rentalRequestDTO.setId(rentalRequest.getId());
        rentalRequestDTO.setRequesterName(rentalRequest.getRequesterName());
        rentalRequestDTO.setRequesterEmail(rentalRequest.getRequesterEmail());
        rentalRequestDTO.setSubmissionTime(rentalRequest.getSubmissionTime());
        rentalRequestDTO.setStartDate(rentalRequest.getStartDate());
        rentalRequestDTO.setEndDate(rentalRequest.getEndDate());
        rentalRequestDTO.setRequesterComments(rentalRequest.getRequesterComments());
        rentalRequestDTO.setAdminComments(rentalRequest.getAdminComments());
        rentalRequestDTO.setStatus(rentalRequest.getStatus());
        rentalRequestDTO.setCheckoutRecord(rentalRequest.getCheckoutRecord() == null ? null : rentalRequest.getCheckoutRecord().getId());
        rentalRequestDTO.setRequestedInventory(rentalRequest.getRequestedInventory().stream()
                .map(inventoryItem -> inventoryItem.getId())
                .toList());
        return rentalRequestDTO;
    }

    private RentalRequest mapToEntity(final RentalRequestDTO rentalRequestDTO,
            final RentalRequest rentalRequest) {
        rentalRequest.setRequesterName(rentalRequestDTO.getRequesterName());
        rentalRequest.setRequesterEmail(rentalRequestDTO.getRequesterEmail());
        rentalRequest.setSubmissionTime(rentalRequestDTO.getSubmissionTime());
        rentalRequest.setStartDate(rentalRequestDTO.getStartDate());
        rentalRequest.setEndDate(rentalRequestDTO.getEndDate());
        rentalRequest.setRequesterComments(rentalRequestDTO.getRequesterComments());
        rentalRequest.setAdminComments(rentalRequestDTO.getAdminComments());
        rentalRequest.setStatus(rentalRequestDTO.getStatus());
        final CheckoutRecord checkoutRecord = rentalRequestDTO.getCheckoutRecord() == null ? null : checkoutRecordRepository.findById(rentalRequestDTO.getCheckoutRecord())
                .orElseThrow(() -> new NotFoundException("checkoutRecord not found"));
        rentalRequest.setCheckoutRecord(checkoutRecord);
        final List<InventoryItem> requestedInventory = inventoryItemRepository.findAllById(
                rentalRequestDTO.getRequestedInventory() == null ? List.of() : rentalRequestDTO.getRequestedInventory());
        if (requestedInventory.size() != (rentalRequestDTO.getRequestedInventory() == null ? 0 : rentalRequestDTO.getRequestedInventory().size())) {
            throw new NotFoundException("one of requestedInventory not found");
        }
        rentalRequest.setRequestedInventory(new HashSet<>(requestedInventory));
        return rentalRequest;
    }

    public boolean checkoutRecordExists(final Long id) {
        return rentalRequestRepository.existsByCheckoutRecordId(id);
    }

    @EventListener(BeforeDeleteCheckoutRecord.class)
    public void on(final BeforeDeleteCheckoutRecord event) {
        final ReferencedException referencedException = new ReferencedException();
        final RentalRequest checkoutRecordRentalRequest = rentalRequestRepository.findFirstByCheckoutRecordId(event.getId());
        if (checkoutRecordRentalRequest != null) {
            referencedException.setKey("checkoutRecord.rentalRequest.checkoutRecord.referenced");
            referencedException.addParam(checkoutRecordRentalRequest.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteInventoryItem.class)
    public void on(final BeforeDeleteInventoryItem event) {
        // remove many-to-many relations at owning side
        rentalRequestRepository.findAllByRequestedInventoryId(event.getId()).forEach(rentalRequest ->
                rentalRequest.getRequestedInventory().removeIf(inventoryItem -> inventoryItem.getId().equals(event.getId())));
    }

}

