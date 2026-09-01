package edu.wisc.wud.games.wud_games_website.util;

import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import edu.wisc.wud.games.wud_games_website.board_game.BoardGameDTO;
import edu.wisc.wud.games.wud_games_website.board_game.BoardGameService;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordDTO;
import edu.wisc.wud.games.wud_games_website.checkout_record.CheckoutRecordService;
import edu.wisc.wud.games.wud_games_website.config.DataInitializer;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisDTO;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisService;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemDTO;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemService;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
@Transactional(rollbackFor = Exception.class)
public class CsvService {
    /*
    private static final ArrayList<String> CSV_HEADERS = new ArrayList<>();
    static {
        CSV_HEADERS.add("Name");
        CSV_HEADERS.add("Description");
        CSV_HEADERS.add("Box Art URL");
        CSV_HEADERS.add("Min Playtime");
        CSV_HEADERS.add("Max Playtime");
        CSV_HEADERS.add("Min Players");
        CSV_HEADERS.add("Max Players");
        CSV_HEADERS.add("Quantity");
        CSV_HEADERS.add("Checkout Count");
        CSV_HEADERS.add("Internal Notes");
    }
    */

    private final BoardGameDisService boardGameDisService;
    private final BoardGameService boardGameService;

    private final GeneralDisService generalDisService;
    private final InventoryItemService inventoryItemService;

    private final CheckoutRecordService checkoutRecordService;

    public CsvService(BoardGameDisService boardGameDisService, BoardGameService boardGameService,
            GeneralDisService generalDisService, InventoryItemService inventoryItemService, CheckoutRecordService checkoutRecordService) {
        this.boardGameDisService = boardGameDisService;
        this.boardGameService = boardGameService;

        this.generalDisService = generalDisService;
        this.inventoryItemService = inventoryItemService;

        this.checkoutRecordService = checkoutRecordService;
    }

    public void importBoardGamesFromCsv(CSVReader reader) throws CsvValidationException, IOException {
        // Read header
        String[] firstLine = reader.readNext();
        System.out.println("First line is:\n" + firstLine);
        Map<String, Integer> indexByHeader = new HashMap<>();
        for (int index = 0; index < firstLine.length; index++) {
            String header = firstLine[index];
            indexByHeader.put(header, index);
        }
        reader.spliterator().forEachRemaining(line -> processOneLine(line, indexByHeader));
    }

    private void processOneLine(String[] line, Map<String, Integer> indexByHeader) {
        GeneralDisDTO description = generalDisService.get(importDescription(line, indexByHeader));

        // Check current number of copies and update accordingly
        List<InventoryItemDTO> items = importCopies(description, line, indexByHeader);
        // Check number of checkout records and update accordingly
        importCheckoutRecords(description, items, line, indexByHeader);
    }

    private Long importDescription(String[] line, Map<String, Integer> indexByHeader) {
        String name = line[indexByHeader.get("Name")];
        // Check if description exists by name
        BoardGameDisDTO description = boardGameDisService.findByName(name);
        if (null == description) {
            description = new BoardGameDisDTO();
        }
        // Create/update description as needed
        if (null != line[indexByHeader.get("Name")]) {
            description.setName(line[indexByHeader.get("Name")]);
        }
        if (null != line[indexByHeader.get("Description")]) {
            description.setDescription(line[indexByHeader.get("Description")]);
        }
        if (null != line[indexByHeader.get("Box Art URL")]) {
            description.setImageUrl(line[indexByHeader.get("Box Art URL")]);
        }
        if (null != line[indexByHeader.get("Min Playtime")]) {
            try {
                description.setMinPlaytime(Integer.valueOf(line[indexByHeader.get("Min Playtime")]));
            } catch (NumberFormatException e) {
            }
        }
        if (null != line[indexByHeader.get("Max Playtime")]) {
            try {
                description.setMaxPlaytime(Integer.valueOf(line[indexByHeader.get("Max Playtime")]));
            } catch (NumberFormatException e) {
            }
        }
        if (null != line[indexByHeader.get("Min Players")]) {
            try {
                description.setMinPlayers(Integer.valueOf(line[indexByHeader.get("Min Players")]));
            } catch (NumberFormatException e) {
            }
        }
        if (null != line[indexByHeader.get("Max Players")]) {
            try {
                description.setMaxPlayers(Integer.valueOf(line[indexByHeader.get("Max Players")]));
            } catch (NumberFormatException e) {
            }
        }
        return boardGameDisService.createOrUpdate(description);
    }

    private List<InventoryItemDTO> importCopies(GeneralDisDTO description, String[] line, Map<String, Integer> indexByHeader) {
        List<InventoryItemDTO> items = new ArrayList<>(inventoryItemService.findByGenDis(description));
        int copiesImported = Integer.valueOf(line[indexByHeader.get("Quantity")]);
        for (int copiesAdded = 0; items.size() < copiesImported; copiesAdded++) {
            BoardGameDTO newCopy = new BoardGameDTO();
            // location
            // notes
            newCopy.setNotes(line[indexByHeader.get("Internal Notes")]);
            // genDis
            newCopy.setGenDis(description);
            // Add copies to the database
            items.add(inventoryItemService.get(inventoryItemService.create(newCopy)));
        }
        return items;
    }

    private void importCheckoutRecords(GeneralDisDTO description, List<InventoryItemDTO> items, String[] line, Map<String, Integer> indexByHeader) {
        int currentLegacyCheckouts = generalDisService.getTotalNumberOfLegacyCheckouts(description.getId());
        int checkoutsImported = Integer.valueOf(line[indexByHeader.get("Checkout Count")]);
        for (int checkoutsAdded = 0; checkoutsAdded + currentLegacyCheckouts < checkoutsImported; checkoutsAdded++) {
            InventoryItemDTO item = items.get(checkoutsAdded % items.size());
            CheckoutRecordDTO record = new CheckoutRecordDTO();
            Set<InventoryItemDTO> inventoryItems = new HashSet<>();
            inventoryItems.add(item);
            record.setInventoryItems(inventoryItems);
            record.setCheckoutTime(DataInitializer.TIME_FOR_LEGACY_RECORDS);
            record.setReturnedTime(DataInitializer.TIME_FOR_LEGACY_RECORDS);
            checkoutRecordService.create(record);
        }
    }
}
