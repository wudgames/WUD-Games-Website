package edu.wisc.wud.games.wud_games_website.general_dis;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class EntityMapper<entityType, dtoType> {
    private final EntityMapper parentMapper;
    private final Class<entityType> entityClass;
    private final Class<dtoType> dtoClass;
    private final Supplier<entityType> entityTypeSupplier;
    private final Supplier<dtoType> dtoSupplier;
    private final Set<EntityMapper<entityType, dtoType>> childMappers = new HashSet<>();

    public EntityMapper(EntityMapper parentMapper, Supplier<entityType> entityTypeSupplier, Supplier<dtoType> dtoSupplier) {
        this.parentMapper = parentMapper;
        // System.out.println("Starting " + this.getClass() + "");
        if (parentMapper != null) {
            parentMapper.childMappers.add(this);
        }
        this.entityTypeSupplier = entityTypeSupplier;
        this.dtoSupplier = dtoSupplier;

        this.entityClass = (Class<entityType>) entityTypeSupplier.get().getClass();
        this.dtoClass = (Class<dtoType>) dtoSupplier.get().getClass();
        // System.out.println("Finished " + this.getClass() + "");
    }

    private EntityMapper<entityType, dtoType> getLeafByEntity(final entityType entity) {
        //System.out.println("Ruining getLeafMapper in " + this.getClass().getSimpleName() + " with entity: "
        //        + entity.getClass().getSimpleName());
        for (EntityMapper<entityType, dtoType> childMapper : childMappers) {
            Boolean isValidMapper = childMapper.entityClass.isAssignableFrom(entity.getClass())
                    || childMapper.entityClass == entity.getClass();
            //System.out.println("    testing " + childMapper.entityClass + " : " + isValidMapper);
            if (isValidMapper) {
                return childMapper.getLeafByEntity(entity);
            }
        }
        return this;
    }

    // Mapping to DTO
    public final dtoType toDTO(final entityType entity) {
        if (entity == null) {
            return null;
        }
        EntityMapper leaf = getLeafByEntity(entity);
        return (dtoType) leaf.bubbleUpDTO(entity, leaf.dtoSupplier.get());
    }

    public final List<dtoType> allToDTO(final List<entityType> entityList) {
        return entityList.stream().map(this::toDTO).toList();
    }

    public final Set<dtoType> allToDTO(final Set<entityType> entityList) {
        return entityList.stream().map(this::toDTO).collect(Collectors.toSet());
    }

    private dtoType bubbleUpDTO(final entityType entity, final dtoType dto) {
        dtoType outputDTO = dto;
        if (parentMapper != null) {
            outputDTO = (dtoType) parentMapper.bubbleUpDTO(entity, dto);
        }
        //System.out.println("Ruining bubbleUpDTO in " + this.getClass().getSimpleName() + " with dto "
        //        + dto.getClass().getSimpleName());
        return localToDTO(entity, outputDTO);
    }

    protected abstract dtoType localToDTO(final entityType entity, final dtoType dto);

    // Mapping to Entity

    private EntityMapper<entityType, dtoType> getLeafByDTO(final dtoType dto) {
        //System.out.println("Ruining getLeafMapper in " + this.getClass().getSimpleName() + " with DTO: "
        //        + dto.getClass().getSimpleName());
        for (EntityMapper<entityType, dtoType> childMapper : childMappers) {
            Boolean isValidMapper = childMapper.dtoClass.isAssignableFrom(dto.getClass())
                    || childMapper.dtoClass == dto.getClass();
            //System.out.println("    testing " + childMapper.dtoClass + " : " + isValidMapper);
            if (isValidMapper) {
                return childMapper.getLeafByDTO(dto);
            }
        }
        return this;
    }
    
    public final entityType toEntity(final dtoType dto) {
        if (dto == null) {
            return null;
        }
        EntityMapper leaf = getLeafByDTO(dto);
        return (entityType) leaf.bubbleUpEntity(dto, leaf.entityTypeSupplier.get());
    }

    public final List<entityType> allToEntity(final List<dtoType> dtoList) {
        return dtoList.stream().map(this::toEntity).toList();
    }

    public final Set<entityType> allToEntity(final Set<dtoType> dtoList) {
        return dtoList.stream().map(this::toEntity).collect(Collectors.toSet());
    }

    private entityType bubbleUpEntity(final dtoType dto, final entityType entity) {
        entityType outputEntity = entity;
        if (parentMapper != null) {
            outputEntity = (entityType) parentMapper.bubbleUpEntity(dto, entity);
        }
        //System.out.println("Ruining bubbleUpEntity in " + this.getClass().getSimpleName() + " with entity "
        //        + entity.getClass().getSimpleName());
        return localToEntity(dto, outputEntity);
    }

    protected abstract entityType localToEntity(final dtoType dto, final entityType entity);
}
