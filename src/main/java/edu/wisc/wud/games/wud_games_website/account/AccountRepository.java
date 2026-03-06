package edu.wisc.wud.games.wud_games_website.account;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findFirstByCurrentCheckoutId(Long id);

    Account findFirstByAccountDisId(Long id);

}

