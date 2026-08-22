package edu.wisc.wud.games.wud_games_website.physical_item;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.location.LocationMapper;

@Component
public class PhysicalItemMapper extends EntityMapper<PhysicalItem, PhysicalItemDTO> {
    private final LocationMapper locationMapper;

    public PhysicalItemMapper(InventoryItemMapper inventoryItemMapper, LocationMapper locationMapper) {
        super(inventoryItemMapper, () -> new PhysicalItem(), () -> new PhysicalItemDTO());
        this.locationMapper = locationMapper;
    }

    @Override
    public PhysicalItemDTO localToDTO(PhysicalItem entity, PhysicalItemDTO dto) {
        dto.setLocation(locationMapper.toDTO(entity.getLocation()));
        return dto;
    }

    @Override
    public PhysicalItem localToEntity(PhysicalItemDTO dto, PhysicalItem entity) {
        entity.setLocation(locationMapper.toEntity(dto.getLocation()));
        return entity;
    }
    
}
