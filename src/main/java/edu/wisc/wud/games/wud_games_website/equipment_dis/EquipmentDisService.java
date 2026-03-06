package edu.wisc.wud.games.wud_games_website.equipment_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteEquipmentDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EquipmentDisService {

    private final EquipmentDisRepository equipmentDisRepository;
    private final ApplicationEventPublisher publisher;

    public EquipmentDisService(final EquipmentDisRepository equipmentDisRepository,
            final ApplicationEventPublisher publisher) {
        this.equipmentDisRepository = equipmentDisRepository;
        this.publisher = publisher;
    }

    public List<EquipmentDisDTO> findAll() {
        final List<EquipmentDis> equipmentDises = equipmentDisRepository.findAll(Sort.by("id"));
        return equipmentDises.stream()
                .map(equipmentDis -> mapToDTO(equipmentDis, new EquipmentDisDTO()))
                .toList();
    }

    public EquipmentDisDTO get(final Long id) {
        return equipmentDisRepository.findById(id)
                .map(equipmentDis -> mapToDTO(equipmentDis, new EquipmentDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EquipmentDisDTO equipmentDisDTO) {
        final EquipmentDis equipmentDis = new EquipmentDis();
        mapToEntity(equipmentDisDTO, equipmentDis);
        return equipmentDisRepository.save(equipmentDis).getId();
    }

    public void update(final Long id, final EquipmentDisDTO equipmentDisDTO) {
        final EquipmentDis equipmentDis = equipmentDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(equipmentDisDTO, equipmentDis);
        equipmentDisRepository.save(equipmentDis);
    }

    public void delete(final Long id) {
        final EquipmentDis equipmentDis = equipmentDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteEquipmentDis(id));
        equipmentDisRepository.delete(equipmentDis);
    }

    private EquipmentDisDTO mapToDTO(final EquipmentDis equipmentDis,
            final EquipmentDisDTO equipmentDisDTO) {
        equipmentDisDTO.setId(equipmentDis.getId());
        return equipmentDisDTO;
    }

    private EquipmentDis mapToEntity(final EquipmentDisDTO equipmentDisDTO,
            final EquipmentDis equipmentDis) {
        return equipmentDis;
    }

    public Map<Long, Long> getEquipmentDisValues() {
        return equipmentDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(EquipmentDis::getId, EquipmentDis::getId));
    }

}

