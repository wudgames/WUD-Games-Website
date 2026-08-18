package edu.wisc.wud.games.wud_games_website.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface ElasticsearchDocumentRepository extends ListPagingAndSortingRepository<ElasticsearchDocument, Long> {
    @Highlight(fields = {
        @HighlightField(name = "name"),
        @HighlightField(name = "description")
    })
    SearchHits<ElasticsearchDocument> findByNameOrDescription(String search);
}