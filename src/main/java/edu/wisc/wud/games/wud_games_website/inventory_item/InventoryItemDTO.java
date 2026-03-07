package edu.wisc.wud.games.wud_games_website.inventory_item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryItemDTO {

    private Long id;

    private LocalDate dateAdded;

    @Size(max = 1024)
    private String notes;

    private Long currentCheckout;

    @NotNull
    private Long genDis;

}

