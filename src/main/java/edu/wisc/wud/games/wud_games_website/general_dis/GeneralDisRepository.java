package edu.wisc.wud.games.wud_games_website.general_dis;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface GeneralDisRepository extends JpaRepository<GeneralDis, Long> {

    List<GeneralDis> findAllByTagsId(Long id);

    //@Query("Select description FROM GeneralDis WHERE (min_players BETWEEN :minPlayers AND :maxPlaytime) ORDER BY name ASC")
    //List<GeneralDis> search(String query, int minPlayers, int maxPlaytime);

    @Query(value = "SELECT description FROM GeneralDis description ORDER BY name ASC")
    List<? extends GeneralDis> search(String searchText);

}

