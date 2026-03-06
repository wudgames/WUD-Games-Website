package edu.wisc.wud.games.wud_games_website.general_dis;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GeneralDisDTO {

    private Long id;

    @Size(max = 255)
    private String name;

    @Size(max = 2048)
    private String description;

    @Size(max = 255)
    private String imageUrl;

    private List<Long> tags;

}

