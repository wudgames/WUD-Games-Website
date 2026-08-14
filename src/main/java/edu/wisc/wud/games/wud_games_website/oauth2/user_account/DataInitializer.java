package edu.wisc.wud.games.wud_games_website.oauth2.user_account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

	@Autowired
    private final UserAccountRepository userAccountRepository;

    public DataInitializer(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Bean
    public CommandLineRunner dataLoader() {
        return args -> {
            System.out.println("User itializer started...");

            // Log to verify if the count check is working
            long count = userAccountRepository.count();
            System.out.println("Number of users in the database: " + count);

            if (count == 0) {
                UserAccount defaultUserAccount = new UserAccount();

                defaultUserAccount.setEmail("admin@example.com");
                
                userAccountRepository.save(defaultUserAccount);
                System.out.println("Default UserAccount saved successfully!");
            } else {
                System.out.println("UserAccounts already exist, skipping insertion.");
            }
        };
    }

    
}
