package edu.wisc.wud.games.wud_games_website.tag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
/*
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
*/
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edu.wisc.wud.games.wud_games_website.util.EntityWithId;


@Entity
//@Document(indexName = "tag")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Tag implements EntityWithId {
    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    //@Id
    private Long id;

    @Column(nullable = false)
    //@Field
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}

