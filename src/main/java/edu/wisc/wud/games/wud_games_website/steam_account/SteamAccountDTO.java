package edu.wisc.wud.games.wud_games_website.steam_account;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SteamAccountDTO {

    private Long id;

    @NotNull
    private Long steamAccountDis;

}

