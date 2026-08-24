package edu.wisc.wud.games.wud_games_website.equipment;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItemMapper;

@Component
public class EquipmentMapper extends EntityMapper<Equipment, EquipmentDTO> {

    public EquipmentMapper(PhysicalItemMapper parentMapper) {
        super(parentMapper, () -> new Equipment(), () -> new EquipmentDTO());
    }

    @Override
    protected EquipmentDTO localToDTO(Equipment entity, EquipmentDTO dto) {
        return dto;
    }

    @Override
    protected Equipment localToEntity(EquipmentDTO dto, Equipment entity) {
        return entity;
    }
    
}
