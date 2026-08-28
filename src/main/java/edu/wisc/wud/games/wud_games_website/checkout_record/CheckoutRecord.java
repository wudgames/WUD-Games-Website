package edu.wisc.wud.games.wud_games_website.checkout_record;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import edu.wisc.wud.games.wud_games_website.util.EntityWithId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;



@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CheckoutRecord implements EntityWithId {

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
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime checkoutTime;

    @Column
    private OffsetDateTime returnedTime;

    @Column
    private Integer peoplePlaying;

    @Column
    private String recipientName;

    @ManyToMany
    @JoinTable(
            name = "ItemsByCheckoutRecord",
            joinColumns = @JoinColumn(name = "checkoutRecordId"),
            inverseJoinColumns = @JoinColumn(name = "inventoryItemId")
    )
    private Set<InventoryItem> inventoryItems = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}

