package edu.wisc.wud.games.wud_games_website.tag;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagResource {

    private final TagService tagService;

    @Autowired
    ElasticsearchOperations operations;  

    public TagResource(final TagService tagService) {
        this.tagService = tagService;
    }

    //@PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/elasticsearch/{name}")
    public ResponseEntity<List<TagDTO>> searchTagsWithElasticsearch(@PathVariable(name = "name") final String name) {
        Criteria criteria = new Criteria("name")
            .fuzzy(name);
        CriteriaQuery query = new CriteriaQuery(criteria);
        return ResponseEntity.ok(operations.search(query, Tag.class)
            .map(searchHit -> {
                Tag tag = searchHit.getContent();
                return tagService.get(tag.getId());
            })
            .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTag(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(tagService.get(id));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createTag(@RequestBody @Valid final TagDTO tagDTO) {
        final Long createdId = tagService.create(tagDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateTag(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final TagDTO tagDTO) {
        tagService.update(id, tagDTO);
        return ResponseEntity.ok(id);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "id") final Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

