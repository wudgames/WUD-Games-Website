package edu.wisc.wud.games.wud_games_website.checkout_record;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemSubclassMapper;

@Component
public class CheckoutRecordMapper extends EntityMapper<CheckoutRecord, CheckoutRecordDTO> {

    private final InventoryItemSubclassMapper inventoryItemSubclassMapper;

    public CheckoutRecordMapper(final InventoryItemSubclassMapper inventoryItemSubclassMapper) {
        super(null, CheckoutRecord.class);
        this.inventoryItemSubclassMapper = inventoryItemSubclassMapper;
    }

    @Override
    public CheckoutRecordDTO localToDTO(CheckoutRecord entity, CheckoutRecordDTO dto) {
        dto.setId(entity.getId());
        dto.setCheckoutTime(entity.getCheckoutTime());
        dto.setReturnedTime(entity.getReturnedTime());
        dto.setPeoplePlaying(entity.getPeoplePlaying());
        dto.setRecipientName(entity.getRecipientName());
        dto.setInventoryItems(entity.getInventoryItems().stream()
                .map(inventoryItem -> inventoryItemSubclassMapper.toDTO(inventoryItem))
                .toList());
        return dto;
    }

    @Override
    public CheckoutRecord localToEntity(CheckoutRecordDTO dto, CheckoutRecord entity) {
        entity.setId(dto.getId());
        entity.setCheckoutTime(dto.getCheckoutTime());
        entity.setReturnedTime(dto.getReturnedTime());
        entity.setPeoplePlaying(dto.getPeoplePlaying());
        entity.setRecipientName(dto.getRecipientName());
        entity.setInventoryItems(dto.getInventoryItems().stream()
                .map(inventoryItem -> inventoryItemSubclassMapper.toEntity(inventoryItem))
                .toList());
        return entity;
    }
}
