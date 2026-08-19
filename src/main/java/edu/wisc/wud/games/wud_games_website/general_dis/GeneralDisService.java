package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import edu.wisc.wud.games.wud_games_website.tag.Tag;
import edu.wisc.wud.games.wud_games_website.tag.TagRepository;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class GeneralDisService {

    private final GeneralDisRepository generalDisRepository;
    private final TagRepository tagRepository;
    private final ApplicationEventPublisher publisher;

    public GeneralDisService(final GeneralDisRepository generalDisRepository,
            final TagRepository tagRepository, final ApplicationEventPublisher publisher) {
        this.generalDisRepository = generalDisRepository;
        this.tagRepository = tagRepository;
        this.publisher = publisher;
    }

    public List<GeneralDisDTO> findAll() {
        final List<GeneralDis> generalDises = generalDisRepository.findAll(Sort.by("id"));
        return generalDises.stream()
                .map(generalDis -> mapToDTO(generalDis, new GeneralDisDTO()))
                .toList();
    }

    public GeneralDisDTO get(final Long id) {
        return generalDisRepository.findById(id)
                .map(generalDis -> mapToDTO(generalDis, new GeneralDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    // This is the root type of the DTO so this can be public
    public Long create(final GeneralDisDTO generalDisDTO) {
        final GeneralDis generalDis = new GeneralDis();
        mapToEntity(generalDisDTO, generalDis);
        return generalDisRepository.save(generalDis).getId();
    }

    public Long create(final GeneralDis generalDis) {
        final GeneralDisDTO generalDisDTO = mapToDTO(generalDis, new GeneralDisDTO());
        return create(generalDisDTO);
    }

    public void update(final Long id, final GeneralDisDTO generalDisDTO) {
        final GeneralDis generalDis = generalDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(generalDisDTO, generalDis);
        generalDisRepository.save(generalDis);
    }

    public void delete(final Long id) {
        final GeneralDis generalDis = generalDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteGeneralDis(id));
        generalDisRepository.delete(generalDis);
    }

    private GeneralDisDTO mapToDTO(final GeneralDis generalDis, final GeneralDisDTO generalDisDTO) {
        generalDisDTO.setId(generalDis.getId());
        generalDisDTO.setName(generalDis.getName());
        generalDisDTO.setDescription(generalDis.getDescription());
        generalDisDTO.setImageUrl(generalDis.getImageUrl());
        generalDisDTO.setTags(generalDis.getTags().stream()
                .map(tag -> tag.getId())
                .toList());
        return generalDisDTO;
    }

    private GeneralDis mapToEntity(final GeneralDisDTO generalDisDTO, final GeneralDis generalDis) {
        generalDis.setName(generalDisDTO.getName());
        generalDis.setDescription(generalDisDTO.getDescription());
        generalDis.setImageUrl(generalDisDTO.getImageUrl());
        final List<Tag> tags = tagRepository.findAllById(
                generalDisDTO.getTags() == null ? List.of() : generalDisDTO.getTags());
        if (tags.size() != (generalDisDTO.getTags() == null ? 0 : generalDisDTO.getTags().size())) {
            throw new NotFoundException("one of tags not found");
        }
        generalDis.setTags(new HashSet<>(tags));
        return generalDis;
    }

    public Map<Long, Long> getGeneralDisValues() {
        return generalDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GeneralDis::getId, GeneralDis::getId));
    }

    @EventListener(BeforeDeleteTag.class)
    public void on(final BeforeDeleteTag event) {
        // remove many-to-many relations at owning side
        generalDisRepository.findAllByTagsId(event.getId()).forEach(generalDis ->
                generalDis.getTags().removeIf(tag -> tag.getId().equals(event.getId())));
    }

}

