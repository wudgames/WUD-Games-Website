package edu.wisc.wud.games.wud_games_website.equipment_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;

@Component
public class EquipmentDisMapper extends EntityMapper<EquipmentDis, EquipmentDisDTO> {

    public EquipmentDisMapper(GeneralDisMapper parentMapper) {
        super(parentMapper, () -> new EquipmentDis(), () -> new EquipmentDisDTO());
    }

    @Override
    protected EquipmentDisDTO localToDTO(EquipmentDis entity, EquipmentDisDTO dto) {
        return dto;
    }

    @Override
    protected EquipmentDis localToEntity(EquipmentDisDTO dto, EquipmentDis entity) {
        return entity;
    }
    
}
