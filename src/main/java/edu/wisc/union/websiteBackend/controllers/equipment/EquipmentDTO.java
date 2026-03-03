package edu.wisc.union.websiteBackend.controllers.equipment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentDTO {
    private Long id;
    private String name;
    private String type;
    private Integer quantity;
    private Integer availableCopies;
    private String description;
    private String imageUrl;
    private String location;
    private Integer checkoutCount;
    private String internalNotes;
    private LocalDateTime createdAt;
}
