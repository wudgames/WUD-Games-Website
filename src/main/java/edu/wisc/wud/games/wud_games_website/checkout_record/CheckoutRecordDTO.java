package edu.wisc.wud.games.wud_games_website.checkout_record;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CheckoutRecordDTO {

    private Long id;

    @NotNull
    private LocalDateTime checkoutTime;

    private LocalDateTime returnedTime;

    private Integer peoplePlaying;

    @Size(max = 255)
    private String resipeantName;

    private List<Long> inventoryItems;

}

