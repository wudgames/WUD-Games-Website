package edu.wisc.wud.games.wud_games_website.equipment_dis;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EquipmentDisService extends EntityService<EquipmentDisRepository, EquipmentDis, EquipmentDisDTO> {
    public EquipmentDisService(EquipmentDisRepository repository, EquipmentDisMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    public Map<Long, Long> getEquipmentDisValues() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EquipmentDis::getId, EquipmentDis::getId));
    }

    @Override
    protected EquipmentDis newEntity() {
        return new EquipmentDis();
    }

    @Override
    public EquipmentDisDTO newDTO() {
        return new EquipmentDisDTO();
    }
}

