package edu.wisc.wud.games.wud_games_website.steam_account;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SteamAccountRepository extends JpaRepository<SteamAccount, Long> {

    SteamAccount findFirstBySteamAccountDisId(Long id);

}

