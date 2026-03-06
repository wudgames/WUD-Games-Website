package edu.wisc.wud.games.wud_games_website.equipment;

import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDis;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDisRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteEquipmentDis;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDisRepository equipmentDisRepository;

    public EquipmentService(final EquipmentRepository equipmentRepository,
            final EquipmentDisRepository equipmentDisRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentDisRepository = equipmentDisRepository;
    }

    public List<EquipmentDTO> findAll() {
        final List<Equipment> equipments = equipmentRepository.findAll(Sort.by("id"));
        return equipments.stream()
                .map(equipment -> mapToDTO(equipment, new EquipmentDTO()))
                .toList();
    }

    public EquipmentDTO get(final Long id) {
        return equipmentRepository.findById(id)
                .map(equipment -> mapToDTO(equipment, new EquipmentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EquipmentDTO equipmentDTO) {
        final Equipment equipment = new Equipment();
        mapToEntity(equipmentDTO, equipment);
        return equipmentRepository.save(equipment).getId();
    }

    public void update(final Long id, final EquipmentDTO equipmentDTO) {
        final Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(equipmentDTO, equipment);
        equipmentRepository.save(equipment);
    }

    public void delete(final Long id) {
        final Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        equipmentRepository.delete(equipment);
    }

    private EquipmentDTO mapToDTO(final Equipment equipment, final EquipmentDTO equipmentDTO) {
        equipmentDTO.setId(equipment.getId());
        equipmentDTO.setEquipmentDis(equipment.getEquipmentDis() == null ? null : equipment.getEquipmentDis().getId());
        return equipmentDTO;
    }

    private Equipment mapToEntity(final EquipmentDTO equipmentDTO, final Equipment equipment) {
        final EquipmentDis equipmentDis = equipmentDTO.getEquipmentDis() == null ? null : equipmentDisRepository.findById(equipmentDTO.getEquipmentDis())
                .orElseThrow(() -> new NotFoundException("equipmentDis not found"));
        equipment.setEquipmentDis(equipmentDis);
        return equipment;
    }

    @EventListener(BeforeDeleteEquipmentDis.class)
    public void on(final BeforeDeleteEquipmentDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final Equipment equipmentDisEquipment = equipmentRepository.findFirstByEquipmentDisId(event.getId());
        if (equipmentDisEquipment != null) {
            referencedException.setKey("equipmentDis.equipment.equipmentDis.referenced");
            referencedException.addParam(equipmentDisEquipment.getId());
            throw referencedException;
        }
    }

}

