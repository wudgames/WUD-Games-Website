package edu.wisc.wud.games.wud_games_website.general_dis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.wisc.wud.games.wud_games_website.account.Account;
import edu.wisc.wud.games.wud_games_website.account_dis.AccountDis;
import edu.wisc.wud.games.wud_games_website.board_game.BoardGame;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_expansion.BoardGameExpansion;
import edu.wisc.wud.games.wud_games_website.board_game_expansion_dis.BoardGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecord;
import edu.wisc.wud.games.wud_games_website.console_account.ConsoleAccount;
import edu.wisc.wud.games.wud_games_website.digital_item.DigitalItem;
import edu.wisc.wud.games.wud_games_website.equipment.Equipment;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccount;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccountDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGame;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameExpansion;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteConsoleAccount;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteDigitalItem;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteEquipment;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteEquipmentDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameConsole;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameConsoleDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteLocation;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeletePhysicalItem;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteRentalRequest;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteSteamAccount;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteSteamAccountDis;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteVideoGame;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteVideoGameDis;
import edu.wisc.wud.games.wud_games_website.game_console.GameConsole;
import edu.wisc.wud.games.wud_games_website.game_console_dis.GameConsoleDis;
import edu.wisc.wud.games.wud_games_website.game_dis.GameDis;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItem;
import edu.wisc.wud.games.wud_games_website.location.Location;
import edu.wisc.wud.games.wud_games_website.physical_item.PhysicalItem;
import edu.wisc.wud.games.wud_games_website.rental_request.RentalRequest;
import edu.wisc.wud.games.wud_games_website.steam_account.SteamAccount;
import edu.wisc.wud.games.wud_games_website.steam_account_dis.SteamAccountDis;
import edu.wisc.wud.games.wud_games_website.tag.Tag;
import edu.wisc.wud.games.wud_games_website.util.EntityWithId;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.video_game.VideoGame;
import edu.wisc.wud.games.wud_games_website.video_game_dis.VideoGameDis;

public abstract class EntityService<repositoryT extends JpaRepository<entityType, Long>, entityType extends EntityWithId, dtoType> {
    protected final repositoryT repository;
    protected final EntityMapper<entityType, dtoType> mapper;
    protected final ApplicationEventPublisher publisher;

    private static Map<Class<? extends EntityWithId>, Function<Long, ? extends BeforeDelete>> classToBeforeDeleteEvent = new HashMap<>();
    static {
        // Descriptions
        classToBeforeDeleteEvent.put(GeneralDis.class, BeforeDeleteGeneralDis::new);
        classToBeforeDeleteEvent.put(GameDis.class, BeforeDeleteGameDis::new);
        classToBeforeDeleteEvent.put(BoardGameDis.class, BeforeDeleteBoardGameDis::new);
        classToBeforeDeleteEvent.put(BoardGameExpansionDis.class, BeforeDeleteBoardGameExpansionDis::new);
        classToBeforeDeleteEvent.put(VideoGameDis.class, BeforeDeleteVideoGameDis::new);
        classToBeforeDeleteEvent.put(EquipmentDis.class, BeforeDeleteEquipmentDis::new);
        classToBeforeDeleteEvent.put(GameConsoleDis.class, BeforeDeleteGameConsoleDis::new);
        classToBeforeDeleteEvent.put(AccountDis.class, BeforeDeleteAccountDis::new);
        classToBeforeDeleteEvent.put(SteamAccountDis.class, BeforeDeleteSteamAccountDis::new);
        // Items
        classToBeforeDeleteEvent.put(InventoryItem.class, BeforeDeleteInventoryItem::new);
        classToBeforeDeleteEvent.put(PhysicalItem.class, BeforeDeletePhysicalItem::new);
        classToBeforeDeleteEvent.put(BoardGame.class, BeforeDeleteBoardGame::new);
        classToBeforeDeleteEvent.put(BoardGameExpansion.class, BeforeDeleteBoardGameExpansion::new);
        classToBeforeDeleteEvent.put(Equipment.class, BeforeDeleteEquipment::new);
        classToBeforeDeleteEvent.put(GameConsole.class, BeforeDeleteGameConsole::new);
        classToBeforeDeleteEvent.put(DigitalItem.class, BeforeDeleteDigitalItem::new);
        classToBeforeDeleteEvent.put(VideoGame.class, BeforeDeleteVideoGame::new);
        classToBeforeDeleteEvent.put(Account.class, BeforeDeleteAccount::new);
        classToBeforeDeleteEvent.put(SteamAccount.class, BeforeDeleteSteamAccount::new);
        classToBeforeDeleteEvent.put(ConsoleAccount.class, BeforeDeleteConsoleAccount::new);
        // Other Entities
        classToBeforeDeleteEvent.put(CheckoutRecord.class, BeforeDeleteCheckoutRecord::new);
        classToBeforeDeleteEvent.put(RentalRequest.class, BeforeDeleteRentalRequest::new);
        classToBeforeDeleteEvent.put(Tag.class, BeforeDeleteTag::new);
        classToBeforeDeleteEvent.put(Location.class, BeforeDeleteLocation::new);
    }

    public EntityService(final repositoryT repository,
            final EntityMapper<entityType, dtoType> mapper,
            final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.publisher = publisher;
    }

    protected abstract entityType newEntity();
    public abstract dtoType newDTO();

    public List<dtoType> findAll() {
        final List<entityType> entities = repository.findAll(Sort.by("id"));
        return entities.stream()
                .map(entity -> mapper.toDTO(entity))
                .toList();
    }

    public dtoType get(final Long id) {
        return repository.findById(id)
                .map(entity -> mapper.toDTO(entity))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final dtoType dto) {
        entityType entity = newEntity();
        entity = mapper.toEntity(dto);
        return repository.save(entity).getId();
    }

    public void update(final Long id, final dtoType dto) {
        entityType entity = repository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity = mapper.toEntity(dto);
        repository.save(entity);
    }

    public void delete(final Long id) {
        final entityType entity = repository.findById(id)
                .orElseThrow(NotFoundException::new);
        Function<Long, ? extends BeforeDelete> eventFactory = classToBeforeDeleteEvent.get(entity.getClass());
        BeforeDelete event = eventFactory.apply(id);
        publisher.publishEvent(event);
        repository.delete(entity);
    }    
}