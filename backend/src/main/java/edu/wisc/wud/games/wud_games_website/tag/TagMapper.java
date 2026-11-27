package edu.wisc.wud.games.wud_games_website.tag;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class TagMapper extends EntityMapper<Tag, TagDTO> {

    public TagMapper() {
        super(null, Tag::new, TagDTO::new);
    }

    @Override
    public TagDTO localToDTO(Tag tag, TagDTO tagDTO) {
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        return tagDTO;
    }

    @Override
    public Tag localToEntity(TagDTO tagDTO, Tag tag) {
        tag.setId(tagDTO.getId());
        tag.setName(tagDTO.getName());
        return tag;
    }
    
}
