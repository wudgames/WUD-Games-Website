package edu.wisc.union.websiteBackend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SteamGameRepository extends JpaRepository<SteamGame, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<SteamGame> findBySteamAppId(String steamAppId);
}
