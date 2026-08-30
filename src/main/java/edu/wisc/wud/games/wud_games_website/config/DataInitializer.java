package edu.wisc.wud.games.wud_games_website.config;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordRepository;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisRepository;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemRepository;
import edu.wisc.wud.games.wud_games_website.location.Location;
import edu.wisc.wud.games.wud_games_website.location.LocationDTO;
import edu.wisc.wud.games.wud_games_website.location.LocationRepository;
import edu.wisc.wud.games.wud_games_website.location.LocationService;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccount;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountDTO;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountRepository;
import edu.wisc.wud.games.wud_games_website.user_account.UserAccountService;

@Component
public class DataInitializer {
    private final BoardGameDisRepository boardGameDisRepository;

    private final UserAccountService userAccountService;
    private final UserAccountRepository userAccountRepository;

    private final LocationRepository locationRepository;

    private final GeneralDisService generalDisService;
    private final GeneralDisRepository generalDisRepository;

    private final InventoryItemRepository inventoryItemRepository;
    private final CheckoutRecordRepository checkoutRecordRepository;

    public DataInitializer(BoardGameDisRepository boardGameDisRepository, UserAccountService userAccountService,
            UserAccountRepository userAccountRepository,
            @Qualifier("GeneralDisService") GeneralDisService generalDisService, LocationRepository locationRepository,
            final CheckoutRecordRepository checkoutRecordRepository,
            final InventoryItemRepository inventoryItemRepository, final GeneralDisRepository generalDisRepository) {
        this.boardGameDisRepository = boardGameDisRepository;
        this.userAccountService = userAccountService;
        this.userAccountRepository = userAccountRepository;
        this.generalDisService = generalDisService;
        this.generalDisRepository = generalDisRepository;
        this.locationRepository = locationRepository;
        this.checkoutRecordRepository = checkoutRecordRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Bean
    public CommandLineRunner dataLoader() {
        return args -> {
            System.out.println("User initialization started...");

            String defaultAdminEmail = System.getenv("DEFAULT_ADMIN_EMAIL");

            // Log to verify if the count check is working
            long count = userAccountRepository.count();
            UserAccount defaultAdminAccount = userAccountRepository.findByEmail(defaultAdminEmail);
            System.out.println("Number of users in the database: " + count);

            if (null == defaultAdminAccount) {
                System.out.println(
                        "Did not find default admin account with email: " + defaultAdminEmail + ", creating...");
                defaultAdminAccount = new UserAccount();
                System.out.println("Default defaultAdminAccount added successfully!");
            } else {
                System.out.println("defaultAdminAccount already exist, skipping insertion.");
            }
            defaultAdminAccount.setEmail(defaultAdminEmail);
            defaultAdminAccount.setHost(true);
            defaultAdminAccount.setAdmin(true);
            defaultAdminAccount.setPhysicalInventoryManager(true);

            userAccountService.update(defaultAdminAccount);

            count = boardGameDisRepository.count();
            System.out.println("Number of game descriptions in the database: " + count);
            if (count == 0) {
                addBoardGamesForTesting();
                System.out.println("Default game descriptions added successfully!");
            }

            if (locationRepository.findByName("Unknown").isEmpty()) {
                Location unknownLocation = locationRepository.findByName("Unknown").orElse(new Location());
                unknownLocation.setName("Unknown");
                locationRepository.save(unknownLocation);
                System.out.println("Created Unknown location successfully!");
            }

            // InventoryItem item = inventoryItemRepository.findAll().get(0);
            // System.out.println(checkoutRecordRepository.getActiveCheckoutFor(item.getId()));

            System.out.println(generalDisRepository.getNumberCheckedOut(Long.valueOf(10001)));
        };
    }

    private void addBoardGamesForTesting() {
        BoardGameDisDTO boardGameDisDTO = new BoardGameDisDTO();
        boardGameDisDTO.setName("7 Wonders");
        boardGameDisDTO
                .setDescription("Draft cards to develop your ancient civilization and build its Wonder of the World.");
        boardGameDisDTO.setImageUrl(
                "https://cf.geekdo-images.com/35h9Za_JvMMMtx_92kT0Jg__imagepage/img/WKlTys0Dc3F6x9r05Fwyvs82tz4=/fit-in/900x600/filters:no_upscale():strip_icc()/pic7149798.jpg");
        generalDisService.create(boardGameDisDTO);
    }
}
