package edu.wisc.wud.games.wud_games_website.physical_item;

import edu.wisc.wud.games.wud_games_website.account_dis.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PhysicalItemDTO {

    private Long id;

    @NotNull
    private ItemStatus avaliblity;

    @NotNull
    private Long location;

}

