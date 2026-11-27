package edu.wisc.wud.games.wud_games_website.events.before_create;

import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeforeCreateCheckoutRecord extends BeforeCreate {
    private CheckoutRecordDTO recordBeingCreated;
}
