package edu.wisc.wud.games.wud_games_website.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
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

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    ElasticsearchClient elasticsearchClient;

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

    public List<ElasticsearchDocument> getAllOfType(Class<ElasticsearchDocument> clasz) {
        Query query = elasticsearchOperations.matchAllQuery();
        SearchHits<ElasticsearchDocument> searchHits = elasticsearchOperations.search(query, clasz);
        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }
    
    public List<ElasticsearchDocument> queryDescriptions(String queryText) {

        try {
            Map<String, IndexMappingRecord> mappingRecords = elasticsearchClient.indices().getMapping().mappings();
            System.out.println("Mapping Records:\n" + mappingRecords);
        } catch (ElasticsearchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("name")
                                .query(queryText)))
                .build();
        SearchHits<ElasticsearchDocument> searchHits = elasticsearchOperations.search(query, ElasticsearchDocument.class);
        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    public void addToSearchDataBase(GeneralDisDTO generalDisDTO) {
        //EmbeddingModel
    }
    
    public ElasticsearchDocument mapToElasticsearchDocument(GeneralDisDTO generalDisDTO) {
        ElasticsearchDocument document = new ElasticsearchDocument();
        document.setId(generalDisDTO.getId());
        document.setName(generalDisDTO.getName());
        document.setDescription(generalDisDTO.getDescription());
        List<Long> tagIds = generalDisDTO.getTags();
        List<String> tagNames = new ArrayList<>();
        tagIds.forEach(id -> {
            tagNames.add(tagRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tag not found for id: " + id)).getName());
        });
        document.setTags(tagNames.toArray(new String[0]));
        // generalDisService.
        // document.setLeaf_type(generalDisService.mapToEntity(generalDisDTO, new
        // GeneralDis()).getClass().toString());
        document.setLeaf_type("WIP");
        return document;
    }
}
