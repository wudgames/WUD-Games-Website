package edu.wisc.wud.games.wud_games_website.checkout_record;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;

@Component
public class CheckoutRecordMapper extends EntityMapper<CheckoutRecord, CheckoutRecordDTO> {

    private final InventoryItemMapper inventoryItemMapper;

    public CheckoutRecordMapper(final InventoryItemMapper inventoryItemMapper) {
        super(null, () -> new CheckoutRecord(), () -> new CheckoutRecordDTO());
        this.inventoryItemMapper = inventoryItemMapper;
    }

    @Override
    public CheckoutRecordDTO localToDTO(CheckoutRecord entity, CheckoutRecordDTO dto) {
        dto.setId(entity.getId());
        dto.setCheckoutTime(entity.getCheckoutTime());
        dto.setReturnedTime(entity.getReturnedTime());
        dto.setPeoplePlaying(entity.getPeoplePlaying());
        dto.setRecipientName(entity.getRecipientName());
        dto.setInventoryItems(inventoryItemMapper.allToDTO(entity.getInventoryItems()));
        return dto;
    }

    @Override
    public CheckoutRecord localToEntity(CheckoutRecordDTO dto, CheckoutRecord entity) {
        entity.setId(dto.getId());
        entity.setCheckoutTime(dto.getCheckoutTime());
        entity.setReturnedTime(dto.getReturnedTime());
        entity.setPeoplePlaying(dto.getPeoplePlaying());
        entity.setRecipientName(dto.getRecipientName());
        entity.setInventoryItems(inventoryItemMapper.allToEntity(dto.getInventoryItems()));
        return entity;
    }
}
