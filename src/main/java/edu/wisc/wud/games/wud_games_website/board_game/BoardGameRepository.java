package edu.wisc.wud.games.wud_games_website.board_game;

import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardGameRepository extends JpaRepository<BoardGame, Long> {

    BoardGame findFirstByBoardGameDisId(Long id);

}

