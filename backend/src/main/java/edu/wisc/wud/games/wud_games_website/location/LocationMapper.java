package edu.wisc.wud.games.wud_games_website.location;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class LocationMapper extends EntityMapper<Location, LocationDTO> {
    public LocationMapper() {
        super(null, () -> new Location(), () -> new LocationDTO());
    }

    @Override
    public LocationDTO localToDTO(Location entity, LocationDTO dto) {
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    @Override
    public Location localToEntity(LocationDTO dto, Location entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
    
}
