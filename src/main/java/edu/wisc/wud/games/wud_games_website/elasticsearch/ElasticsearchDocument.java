package edu.wisc.wud.games.wud_games_website.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "general_descriptions")
public class ElasticsearchDocument {
    
    /* This id should match the id (of the general description) in the database. */
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String leaf_type;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String[] tags;
}
