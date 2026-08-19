package edu.wisc.wud.games.wud_games_website.config;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountDTO;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountRepository;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountService;

@Component
public class DataInitializer {

	private final BoardGameDisRepository boardGameDisRepository;
    private final UserAccountService userAccountService;
    @Autowired
    private final UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameDisService boardGameDisService;

    public DataInitializer(UserAccountRepository userAccountRepository, UserAccountService userAccountService, BoardGameDisRepository boardGameDisRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountService = userAccountService;
        this.boardGameDisRepository = boardGameDisRepository;
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
                UserAccountDTO defaultAdminAccount = new UserAccountDTO();

                defaultAdminAccount.setEmail(defaultAdminEmail);
                defaultAdminAccount.setAdmin(true);
                
                userAccountService.create(defaultAdminAccount);
                System.out.println("Default defaultAdminAccount added successfully!");
            } else {
                System.out.println("defaultAdminAccount already exist, skipping insertion.");
            }

            count = boardGameDisRepository.count();
            System.out.println("Number of game descriptions in the database: " + count);
            if (count == 0) {
                addEntriesForTesting();
                System.out.println("Default game descriptions added successfully!");
            }
        };
    }

    private void addEntriesForTesting() {
        BoardGameDis boardGameDis = new BoardGameDis();
        boardGameDis.setName("7 Wonders");
        boardGameDis.setDescription("Draft cards to develop your ancient civilization and build its Wonder of the World.");
        boardGameDis.setImageUrl("https://cf.geekdo-images.com/35h9Za_JvMMMtx_92kT0Jg__imagepage/img/WKlTys0Dc3F6x9r05Fwyvs82tz4=/fit-in/900x600/filters:no_upscale():strip_icc()/pic7149798.jpg");
        boardGameDisService.create(boardGameDis);
    }
}
