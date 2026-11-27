package edu.wisc.wud.games.wud_games_website.user_account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByEmail(String email);

    List<UserAccount> emailContains(String email);
}
