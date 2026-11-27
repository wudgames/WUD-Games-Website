package edu.wisc.wud.games.wud_games_website.equipment;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class EquipmentService extends EntityService<EquipmentRepository, Equipment, EquipmentDTO> {
    
    public EquipmentService(EquipmentRepository repository, EquipmentMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected Equipment newEntity() {
        return new Equipment();
    }

    @Override
    public EquipmentDTO newDTO() {
        return new EquipmentDTO();
    }
}

