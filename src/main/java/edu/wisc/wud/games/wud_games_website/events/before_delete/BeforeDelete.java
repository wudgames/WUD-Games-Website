package edu.wisc.wud.games.wud_games_website.events.before_delete;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class BeforeDelete {
    private Long id;
}
