package edu.wisc.wud.games.wud_games_website.user_account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findByEmail(String email);
}
