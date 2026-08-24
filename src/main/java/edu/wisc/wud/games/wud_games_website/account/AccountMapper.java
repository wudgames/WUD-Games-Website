package edu.wisc.wud.games.wud_games_website.account;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;

@Component
public class AccountMapper extends EntityMapper<Account, AccountDTO> {

    public AccountMapper(InventoryItemMapper parentMapper) {
        super(parentMapper, () -> new Account(), () -> new AccountDTO());
    }

    @Override
    protected AccountDTO localToDTO(Account entity, AccountDTO dto) {
        return dto;
    }

    @Override
    protected Account localToEntity(AccountDTO dto, Account entity) {
        return entity;
    }
    
}
