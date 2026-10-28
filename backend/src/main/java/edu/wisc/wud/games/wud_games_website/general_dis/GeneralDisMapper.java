package edu.wisc.wud.games.wud_games_website.general_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.tag.TagDTO;
import edu.wisc.wud.games.wud_games_website.tag.TagMapper;

@Component
public class GeneralDisMapper extends EntityMapper<GeneralDis, GeneralDisDTO> {

    private final TagMapper tagMapper;

    public GeneralDisMapper(TagMapper tagMapper) {
        super(null, () -> new GeneralDis(), () -> new GeneralDisDTO());
        this.tagMapper = tagMapper;
    }

    public GeneralDisDTO localToDTO(final GeneralDis generalDis,
            final GeneralDisDTO generalDisDTO) {
        generalDisDTO.setId(generalDis.getId());
        generalDisDTO.setName(generalDis.getName());
        generalDisDTO.setDescription(generalDis.getDescription());
        generalDisDTO.setImageUrl(generalDis.getImageUrl());
        generalDisDTO.setTags(generalDis.getTags().stream()
                .map(tag -> tagMapper.toDTO(tag))
                .toList());
        return generalDisDTO;
    }

    public GeneralDis localToEntity(final GeneralDisDTO generalDisDTO,
            final GeneralDis generalDis) {
        generalDis.setId(generalDisDTO.getId());
        generalDis.setName(generalDisDTO.getName());
        generalDis.setDescription(generalDisDTO.getDescription());
        generalDis.setImageUrl(generalDisDTO.getImageUrl());
        return generalDis;
    }
}
