package edu.wisc.wud.games.wud_games_website.video_game;

import org.springframework.data.jpa.repository.JpaRepository;


public interface VideoGameRepository extends JpaRepository<VideoGame, Long> {

    VideoGame findFirstByVideoGameDisId(Long id);

}

