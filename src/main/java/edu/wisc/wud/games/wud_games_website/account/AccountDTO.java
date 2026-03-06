package edu.wisc.wud.games.wud_games_website.account;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AccountDTO {

    private Long id;

    private Long currentCheckout;

    @NotNull
    private Long accountDis;

}

