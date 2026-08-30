package edu.wisc.wud.games.wud_games_website.general_dis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenDisWithAvailabilityDTO {
    private GeneralDisDTO generalDis;
    private int totalCopies;
    private int copiesAvailable;
}
