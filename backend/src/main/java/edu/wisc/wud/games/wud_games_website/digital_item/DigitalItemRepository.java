package edu.wisc.wud.games.wud_games_website.digital_item;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDis;


public interface DigitalItemRepository extends JpaRepository<DigitalItem, Long> {

    Set<DigitalItem> findAllByCompatibleAccountsContaining(AccountDis account);

}

