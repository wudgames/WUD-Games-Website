package edu.wisc.wud.games.wud_games_website.elasticsearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;

public class ElasticsearchDocumentService {
    @Autowired
    private GeneralDisService generalDisService;

    public GeneralDisDTO mapToGeneralDis(ElasticsearchDocument document) {
        return generalDisService.get(document.getId());
    }

    public List<GeneralDisDTO> mapAllToGeneralDis(List<ElasticsearchDocument> documents) {
        return documents.stream().map(this::mapToGeneralDis).collect(Collectors.toList());
    }
}
