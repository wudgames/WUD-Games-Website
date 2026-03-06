package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardGameExpansionRepository extends JpaRepository<BoardGameExpansion, Long> {

    BoardGameExpansion findFirstByGameExpansionDisId(Long id);

}

