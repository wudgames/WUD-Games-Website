package edu.wisc.wud.games.wud_games_website.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.tag.TagRepository;

@Service
public class ElasticsearchDocumentService {
    @Autowired
    private GeneralDisService generalDisService;

    @Autowired
    private TagRepository tagRepository;

    public GeneralDisDTO mapToGeneralDis(ElasticsearchDocument document) {
        System.out.println("finding document by id: " + document.getId());
        return generalDisService.get(document.getId());
    }

    public List<GeneralDisDTO> mapAllToGeneralDis(Iterable<ElasticsearchDocument> iterable) {
        List<GeneralDisDTO> generalDisList = new ArrayList<>();
        for (ElasticsearchDocument document : iterable) {
            generalDisList.add(mapToGeneralDis(document));
        }
        return generalDisList;
    }

    public ElasticsearchDocument mapToElasticsearchDocument(GeneralDisDTO generalDisDTO) {
        ElasticsearchDocument document = new ElasticsearchDocument();
        document.setId(generalDisDTO.getId());
        document.setName(generalDisDTO.getName());
        document.setDescription(generalDisDTO.getDescription());
        List<Long> tagIds = generalDisDTO.getTags();
        List<String> tagNames = new ArrayList<>();
        tagIds.forEach(id -> {tagNames.add(tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found for id: " + id)).getName());});
        document.setTags(tagNames.toArray(new String[0]));
        //generalDisService.
        //document.setLeaf_type(generalDisService.mapToEntity(generalDisDTO, new GeneralDis()).getClass().toString());
        document.setLeaf_type("WIP");
        return document;
    }
}
