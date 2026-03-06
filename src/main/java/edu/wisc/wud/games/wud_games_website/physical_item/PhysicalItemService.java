package edu.wisc.wud.games.wud_games_website.physical_item;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteLocation;
import edu.wisc.wud.games.wud_games_website.location.Location;
import edu.wisc.wud.games.wud_games_website.location.LocationRepository;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PhysicalItemService {

    private final PhysicalItemRepository physicalItemRepository;
    private final LocationRepository locationRepository;

    public PhysicalItemService(final PhysicalItemRepository physicalItemRepository,
            final LocationRepository locationRepository) {
        this.physicalItemRepository = physicalItemRepository;
        this.locationRepository = locationRepository;
    }

    public List<PhysicalItemDTO> findAll() {
        final List<PhysicalItem> physicalItems = physicalItemRepository.findAll(Sort.by("id"));
        return physicalItems.stream()
                .map(physicalItem -> mapToDTO(physicalItem, new PhysicalItemDTO()))
                .toList();
    }

    public PhysicalItemDTO get(final Long id) {
        return physicalItemRepository.findById(id)
                .map(physicalItem -> mapToDTO(physicalItem, new PhysicalItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PhysicalItemDTO physicalItemDTO) {
        final PhysicalItem physicalItem = new PhysicalItem();
        mapToEntity(physicalItemDTO, physicalItem);
        return physicalItemRepository.save(physicalItem).getId();
    }

    public void update(final Long id, final PhysicalItemDTO physicalItemDTO) {
        final PhysicalItem physicalItem = physicalItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(physicalItemDTO, physicalItem);
        physicalItemRepository.save(physicalItem);
    }

    public void delete(final Long id) {
        final PhysicalItem physicalItem = physicalItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        physicalItemRepository.delete(physicalItem);
    }

    private PhysicalItemDTO mapToDTO(final PhysicalItem physicalItem,
            final PhysicalItemDTO physicalItemDTO) {
        physicalItemDTO.setId(physicalItem.getId());
        physicalItemDTO.setAvaliblity(physicalItem.getAvaliblity());
        physicalItemDTO.setLocation(physicalItem.getLocation() == null ? null : physicalItem.getLocation().getId());
        return physicalItemDTO;
    }

    private PhysicalItem mapToEntity(final PhysicalItemDTO physicalItemDTO,
            final PhysicalItem physicalItem) {
        physicalItem.setAvaliblity(physicalItemDTO.getAvaliblity());
        final Location location = physicalItemDTO.getLocation() == null ? null : locationRepository.findById(physicalItemDTO.getLocation())
                .orElseThrow(() -> new NotFoundException("location not found"));
        physicalItem.setLocation(location);
        return physicalItem;
    }

    @EventListener(BeforeDeleteLocation.class)
    public void on(final BeforeDeleteLocation event) {
        final ReferencedException referencedException = new ReferencedException();
        final PhysicalItem locationPhysicalItem = physicalItemRepository.findFirstByLocationId(event.getId());
        if (locationPhysicalItem != null) {
            referencedException.setKey("location.physicalItem.location.referenced");
            referencedException.addParam(locationPhysicalItem.getId());
            throw referencedException;
        }
    }

}

