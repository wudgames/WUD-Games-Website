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
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import edu.wisc.wud.games.wud_games_website.console_account.ConsoleAccount;
import edu.wisc.wud.games.wud_games_website.digital_item.DigitalItem;
import edu.wisc.wud.games.wud_games_website.equipment.Equipment;
import edu.wisc.wud.games.wud_games_website.equipment_dis.EquipmentDis;
import edu.wisc.wud.games.wud_games_website.events.before_create.BeforeCreate;
import edu.wisc.wud.games.wud_games_website.events.before_create.BeforeCreateCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteAccount;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteAccountDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteBoardGame;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteBoardGameExpansion;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteBoardGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteCheckoutRecord;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteConsoleAccount;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteDigitalItem;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteEquipment;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteEquipmentDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteGameConsole;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteGameConsoleDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteGameDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteGeneralDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteInventoryItem;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteLocation;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeletePhysicalItem;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteRentalRequest;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteSteamAccount;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteSteamAccountDis;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteVideoGame;
import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteVideoGameDis;
import edu.wisc.wud.games.wud_games_website.events.brfore_update.BeforeUpdate;
import edu.wisc.wud.games.wud_games_website.events.brfore_update.BeforeUpdateCheckoutRecord;
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

    private static Map<Class<?>, Function<Long, ? extends BeforeUpdate>> classToBeforeUpdateEvent = new HashMap<>();
    static {
        classToBeforeUpdateEvent.put(CheckoutRecordDTO.class, BeforeUpdateCheckoutRecord::new);
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
        if (dto.getClass() == CheckoutRecordDTO.class) {
            BeforeCreate event = new BeforeCreateCheckoutRecord((CheckoutRecordDTO) dto);
            publisher.publishEvent(event);
        }
        entityType entity = newEntity();
        entity = mapper.toEntity(dto);
        return repository.save(entity).getId();
    }

    public void update(final Long id, final dtoType dto) {
        entityType entity = repository.findById(id)
                .orElseThrow(NotFoundException::new);
        entity = mapper.toEntity(dto);
        if (classToBeforeUpdateEvent.containsKey(entity.getClass())) {
            Function<Long, ? extends BeforeUpdate> eventFactory = classToBeforeUpdateEvent.get(entity.getClass());
            BeforeUpdate event = eventFactory.apply(id);
            publisher.publishEvent(event);
        }
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