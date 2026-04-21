package edu.wisc.wud.games.wud_games_website.inventory_item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryItemDTO {

    private Long id;

    private OffsetDateTime dateAdded;

    @Size(max = 1024)
    private String notes;

    private Long currentCheckout;

    @NotNull
    private Long genDis;

}

