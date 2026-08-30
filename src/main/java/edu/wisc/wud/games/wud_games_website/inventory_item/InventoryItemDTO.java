package edu.wisc.wud.games.wud_games_website.inventory_item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryItemDTO {
    private Long id;

    private OffsetDateTime dateAdded;

    @Size(max = 1024)
    private String notes;

    @NotNull
    private GeneralDisDTO genDis;

    // This only exist on the DTO, and my be null
    private CheckoutRecordDTO currentCheckoutRecord;
}