package edu.wisc.wud.games.wud_games_website.board_game_dis;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDis;

public interface BoardGameDisRepository extends JpaRepository<BoardGameDis, Long> {

    @Query(value = "SELECT description FROM BoardGameDis description " +
        "WHERE description.name LIKE %:searchText% AND description.minPlayers <= :players AND description.maxPlayers >= :players " + 
        "AND description.maxPlaytime <= :playtime ORDER BY name ASC")
    List<? extends BoardGameDis> search(String searchText, int players, int playtime);

}
