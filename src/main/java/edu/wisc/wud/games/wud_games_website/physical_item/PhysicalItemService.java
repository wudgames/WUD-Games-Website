package edu.wisc.wud.games.wud_games_website.physical_item;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteLocation;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PhysicalItemService extends EntityService<PhysicalItemRepository, PhysicalItem, PhysicalItemDTO> {

    public PhysicalItemService(PhysicalItemRepository repository, EntityMapper<PhysicalItem, PhysicalItemDTO> mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected PhysicalItem newEntity() {
        return new PhysicalItem();
    }

    @Override
    public PhysicalItemDTO newDTO() {
        return new PhysicalItemDTO();
    }

    @EventListener(BeforeDeleteLocation.class)
    public void on(final BeforeDeleteLocation event) {
        final ReferencedException referencedException = new ReferencedException();
        final PhysicalItem locationPhysicalItem = repository.findFirstByLocationId(event.getId());
        if (locationPhysicalItem != null) {
            referencedException.setKey("location.physicalItem.location.referenced");
            referencedException.addParam(locationPhysicalItem.getId());
            throw referencedException;
        }
    }

}
