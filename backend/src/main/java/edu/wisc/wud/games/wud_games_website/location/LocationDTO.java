package edu.wisc.wud.games.wud_games_website.location;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LocationDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String name;

}

