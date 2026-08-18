package edu.wisc.wud.games.wud_games_website.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;

@Service
public class ElasticsearchDocumentService {
    @Autowired
    private GeneralDisService generalDisService;

    public GeneralDisDTO mapToGeneralDis(ElasticsearchDocument document) {
        System.out.println("document is: " + document);
        return generalDisService.get(document.getId());
    }

    public List<GeneralDisDTO> mapAllToGeneralDis(Iterable<ElasticsearchDocument> iterable) {
        List<GeneralDisDTO> generalDisList = new ArrayList<>();
        for (ElasticsearchDocument document : iterable) {
            System.out.println("document id is: " + document.getId());
            generalDisList.add(mapToGeneralDis(document));
        }
        System.out.println("#2.2");
        return generalDisList;
    }
}
