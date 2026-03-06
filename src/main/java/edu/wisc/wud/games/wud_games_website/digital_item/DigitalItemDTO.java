package edu.wisc.wud.games.wud_games_website.digital_item;

import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DigitalItemDTO {

    private Long id;
    private List<Long> compatableAccounts;

}

