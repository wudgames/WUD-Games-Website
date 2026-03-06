package edu.wisc.wud.games.wud_games_website.equipment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EquipmentDTO {

    private Long id;

    @NotNull
    private Long equipmentDis;

}

