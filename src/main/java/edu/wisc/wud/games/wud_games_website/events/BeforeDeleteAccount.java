package edu.wisc.wud.games.wud_games_website.events;

import lombok.Getter;

@Getter
public class BeforeDeleteAccount extends BeforeDeleteInventoryItem {

    public BeforeDeleteAccount(Long id) {
        super(id);
        //TODO Auto-generated constructor stub
    }

}

