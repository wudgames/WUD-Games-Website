package edu.wisc.wud.games.wud_games_website.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisRepository;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccount;

@Component
public class ElasticsearchDataInitializer {
    private GeneralDisRepository generalDisRepository;
    private GeneralDisService generalDisService;
    private ElasticsearchDocumentRepository elasticsearchDocumentRepository;
    private ElasticsearchDocumentService elasticsearchDocumentService;

    public ElasticsearchDataInitializer(GeneralDisRepository generalDisRepository,
            GeneralDisService generalDisService,
            ElasticsearchDocumentRepository elasticsearchDocumentRepository,
            ElasticsearchDocumentService elasticsearchDocumentService) {
        this.generalDisRepository = generalDisRepository;
        this.generalDisService = generalDisService;
        this.elasticsearchDocumentRepository = elasticsearchDocumentRepository;
        this.elasticsearchDocumentService = elasticsearchDocumentService;
    }

    public void sendAllDescriptionsToElasticSearch() {
        System.out.println("Loading general descriptions in to elastic search...");

        // Log to verify if the count check is working
        long count = generalDisRepository.count();
        System.out.println("Number of descriptions in the database: " + count);

        List<GeneralDis> generalDescriptions = generalDisRepository.findAll();
        List<ElasticsearchDocument> elasticSearchDocuments = new ArrayList<>();
        for (GeneralDis generalDis : generalDescriptions) {
            GeneralDisDTO generalDisDTO = generalDisService.get(generalDis.getId());
            elasticSearchDocuments.add(elasticsearchDocumentService.mapToElasticsearchDocument(generalDisDTO));
        }

        elasticsearchDocumentRepository.saveAll(elasticSearchDocuments);
    }

}
