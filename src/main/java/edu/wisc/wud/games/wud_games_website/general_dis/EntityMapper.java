package edu.wisc.wud.games.wud_games_website.general_dis;

import java.util.HashSet;
import java.util.Set;

public abstract class EntityMapper<entityType, dtoType> {
    private final EntityMapper parentMapper;
    private final Class<entityType> clasz;
    private final Set<EntityMapper<entityType, dtoType>> childMappers = new HashSet<>();

    public EntityMapper(EntityMapper parentMapper, Class<entityType> clasz) {
        this.parentMapper = parentMapper;
        System.out.println("Starting " + this.getClass() + "");
        if (parentMapper != null) {
            parentMapper.childMappers.add(this);
        }
        this.clasz = clasz;
        System.out.println("Finished " + this.getClass() + "");
    }
    
    private EntityMapper<entityType, dtoType> getLeafMapper(final entityType entity) {
        for (EntityMapper<entityType, dtoType> childMapper : childMappers) {
            if (entity.getClass().isAssignableFrom(childMapper.clasz)) {
                return childMapper.getLeafMapper(entity);
            }
        }
        return this;
    }

    // Mapping to DTO
    public final dtoType toDTO(final entityType entity, final dtoType dto) {
        return getLeafMapper(entity).bubbleUpDTO(entity, dto);
    }

    private dtoType bubbleUpDTO(final entityType entity, final dtoType dto) {
        dtoType outputDTO = dto;
        if (parentMapper != null) {
            outputDTO = (dtoType) parentMapper.toDTO(entity, dto);
        }
        return localToDTO(entity, outputDTO);
    }

    protected abstract dtoType localToDTO(final entityType entity, final dtoType dto);

    // Mapping to Entity
    public final entityType toEntity(final dtoType dto, final entityType entity) {
        return getLeafMapper(entity).bubbleUpEntity(dto, entity);
    }

    private entityType bubbleUpEntity(final dtoType dto, final entityType entity) {
        entityType outputEntity = entity;
        if (parentMapper != null) {
            outputEntity = (entityType) parentMapper.toEntity(dto, entity);
        }
        return localToEntity(dto, outputEntity);
    }

    protected abstract entityType localToEntity(final dtoType dto, final entityType entity);
}
