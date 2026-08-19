package edu.wisc.wud.games.wud_games_website.config;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccount;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountDTO;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountRepository;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountService;

@Component
public class DataInitializer {
    private final BoardGameDisRepository boardGameDisRepository;

    private final UserAccountService userAccountService;

    private final UserAccountRepository userAccountRepository;

    private final BoardGameDisService boardGameDisService;

    public DataInitializer(BoardGameDisRepository boardGameDisRepository, UserAccountService userAccountService,
            UserAccountRepository userAccountRepository, BoardGameDisService boardGame) {
        this.boardGameDisRepository = boardGameDisRepository;
        this.userAccountService = userAccountService;
        this.userAccountRepository = userAccountRepository;
        this.boardGameDisService = boardGame;
    }

    @Bean
    public CommandLineRunner dataLoader() {
        return args -> {
            System.out.println("User initialization started...");

            String defaultAdminEmail = System.getenv("DEFAULT_ADMIN_EMAIL");

            // Log to verify if the count check is working
            long count = userAccountRepository.count();
            UserAccount defaultAdminAccount = userAccountRepository.findByEmail(defaultAdminEmail);
            Long id;
            System.out.println("Number of users in the database: " + count);

            if (null == defaultAdminAccount) {
                System.out.println(
                        "Did not find default admin account with email: " + defaultAdminEmail + ", creating...");
                id = userAccountService.create(new UserAccountDTO());
                defaultAdminAccount = userAccountRepository.findById(id).orElseThrow();
                System.out.println("Default defaultAdminAccount added successfully!");
            } else {
                id = defaultAdminAccount.getId();
                System.out.println("defaultAdminAccount already exist, skipping insertion.");
            }
            defaultAdminAccount.setEmail(defaultAdminEmail);
            defaultAdminAccount.setAdmin(true);
            defaultAdminAccount.setPhysicalInventoryManager(true);

            userAccountService.update(defaultAdminAccount);

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
        boardGameDis
                .setDescription("Draft cards to develop your ancient civilization and build its Wonder of the World.");
        boardGameDis.setImageUrl(
                "https://cf.geekdo-images.com/35h9Za_JvMMMtx_92kT0Jg__imagepage/img/WKlTys0Dc3F6x9r05Fwyvs82tz4=/fit-in/900x600/filters:no_upscale():strip_icc()/pic7149798.jpg");
        boardGameDisService.create(boardGameDis);
    }
}
