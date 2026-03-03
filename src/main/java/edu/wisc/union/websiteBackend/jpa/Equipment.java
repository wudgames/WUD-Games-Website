package edu.wisc.union.websiteBackend.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
@Getter
@Setter
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_gen")
    @SequenceGenerator(name = "equipment_gen", sequenceName = "equipment_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String type; // CONTROLLER, JOYCON, RPG_EQUIPMENT, OTHER

    private Integer quantity;
    private Integer availableCopies;

    @Column(length = 1024, columnDefinition = "VARCHAR(1024)")
    @Size(max = 1024)
    private String description;

    private String imageUrl;
    private String location;
    private Integer checkoutCount;
    private String internalNotes;

    @Column(updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;
}
