package edu.wisc.wud.games.wud_games_website.account_dis;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountDisDTO extends GeneralDisDTO {

    @NotNull
    @Size(max = 255)
    private String username;

    private String notes;

}

