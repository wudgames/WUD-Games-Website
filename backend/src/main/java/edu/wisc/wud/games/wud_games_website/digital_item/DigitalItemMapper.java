package edu.wisc.wud.games.wud_games_website.digital_item;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDisMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;

@Component
public class DigitalItemMapper extends EntityMapper<DigitalItem, DigitalItemDTO> {

    private final AccountDisMapper accountDisMapper;

    public DigitalItemMapper(InventoryItemMapper parentMapper, AccountDisMapper accountDisMapper) {
        super(parentMapper, () -> new DigitalItem(), () -> new DigitalItemDTO());
        this.accountDisMapper = accountDisMapper;
    }

    @Override
    protected DigitalItemDTO localToDTO(DigitalItem entity, DigitalItemDTO dto) {
        dto.setCompatibleAccounts(accountDisMapper.allToDTO(entity.getCompatibleAccounts()));
        return dto;
    }

    @Override
    protected DigitalItem localToEntity(DigitalItemDTO dto, DigitalItem entity) {
        entity.setCompatibleAccounts(accountDisMapper.allToEntity(dto.getCompatibleAccounts()));
        return entity;
    }
}
