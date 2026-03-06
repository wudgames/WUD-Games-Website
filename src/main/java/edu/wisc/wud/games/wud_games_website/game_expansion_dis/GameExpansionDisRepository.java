package edu.wisc.wud.games.wud_games_website.game_expansion_dis;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GameExpansionDisRepository extends JpaRepository<GameExpansionDis, Long> {

    GameExpansionDis findFirstByBaseGameDisId(Long id);

}

