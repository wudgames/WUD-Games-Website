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
            System.out.println("User initialization started...");

            // Log to verify if the count check is working
            long count = userAccountRepository.count();
            System.out.println("Number of users in the database: " + count);

            String defaultAdminEmail = System.getenv("DEFAULT_ADMIN_EMAIL");

            if (null == userAccountRepository.findByEmail(defaultAdminEmail)) {
                System.out.println("userAccountRepository.findByEmail(defaultAdminEmail) = " + userAccountRepository.findByEmail(defaultAdminEmail));
                System.out.println("Did not find default admin account with email: " + defaultAdminEmail + ", creating...");
                UserAccount defaultAdminAccount = new UserAccount();

                defaultAdminAccount.setEmail(defaultAdminEmail);
                defaultAdminAccount.setAdmin(true);
                
                userAccountRepository.save(defaultAdminAccount);
                System.out.println("Default defaultAdminAccount added successfully!");
            } else {
                System.out.println("defaultAdminAccount already exist, skipping insertion.");
            }
        };
    }

    
}
