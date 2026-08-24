package edu.wisc.wud.games.wud_games_website.inventory_item;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;

@Component
public class InventoryItemMapper extends EntityMapper<InventoryItem, InventoryItemDTO> {

    private final GeneralDisMapper generalDisMapper;

    public InventoryItemMapper(GeneralDisMapper generalDisMapper) {
        super(null, () -> new InventoryItem(), () -> new InventoryItemDTO());
        this.generalDisMapper = generalDisMapper;
    }

    @Override
    public InventoryItemDTO localToDTO(InventoryItem entity, InventoryItemDTO dto) {
        dto.setId(entity.getId());
        dto.setDateAdded(entity.getDateAdded());
        dto.setNotes(entity.getNotes());
        dto.setGenDis(generalDisMapper.toDTO(entity.getGenDis()));
        // TODO dateCreated and lastUpdated
        return dto;
    }

    @Override
    public InventoryItem localToEntity(InventoryItemDTO dto, InventoryItem entity) {
        entity.setId(dto.getId());
        entity.setDateAdded(dto.getDateAdded());
        entity.setNotes(dto.getNotes());
        entity.setGenDis(generalDisMapper.toEntity(dto.getGenDis()));
        // TODO dateCreated and lastUpdated
        return entity;
    }
    
}
