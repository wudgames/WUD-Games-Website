package edu.wisc.wud.games.wud_games_website.general_dis;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.wisc.wud.games.wud_games_website.events.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.util.EntityWithId;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;

public abstract class EntityService<repositoryT extends JpaRepository<entityType, Long>, entityType extends EntityWithId, dtoType> {
    protected final repositoryT repository;
    protected final EntityMapper<entityType, dtoType> mapper;
    protected final ApplicationEventPublisher publisher;

    public EntityService(final repositoryT repository,
            final EntityMapper<entityType, dtoType> mapper,
            final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.publisher = publisher;
    }

    protected abstract entityType newEntity();
    public abstract dtoType newDTO();

    public List<dtoType> findAll() {
        final List<entityType> entities = repository.findAll(Sort.by("id"));
        return entities.stream()
                .map(entity -> mapper.toDTO(entity))
                .toList();
    }

    public dtoType get(final Long id) {
        return repository.findById(id)
                .map(entity -> mapper.toDTO(entity))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final dtoType dto) {
        final entityType entity = newEntity();
        mapper.toEntity(dto);
        return repository.save(entity).getId();
    }

    public void update(final Long id, final dtoType dto) {
        final entityType entity = repository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapper.toEntity(dto);
        repository.save(entity);
    }

    public void delete(final Long id) {
        final entityType entity = repository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDelete<entityType>(id));
        repository.delete(entity);
    }    
}