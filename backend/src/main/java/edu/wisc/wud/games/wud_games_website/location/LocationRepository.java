package edu.wisc.wud.games.wud_games_website.location;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationRepository extends JpaRepository<Location, Long> {
    public Optional<Location> findByName(String name);
}

