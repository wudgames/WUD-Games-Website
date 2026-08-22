package edu.wisc.wud.games.wud_games_website.checkout_record;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CheckoutRecordDTO {

    private Long id;

    @NotNull
    private OffsetDateTime checkoutTime;

    private OffsetDateTime returnedTime;

    private Integer peoplePlaying;

    @Size(max = 255)
    private String recipientName;

    private List<InventoryItemDTO> inventoryItems;

}

