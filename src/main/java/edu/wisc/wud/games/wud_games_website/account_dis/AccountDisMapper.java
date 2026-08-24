package edu.wisc.wud.games.wud_games_website.account_dis;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.GeneralDisMapper;

@Component
public class AccountDisMapper extends EntityMapper<AccountDis, AccountDisDTO> {

    public AccountDisMapper(GeneralDisMapper parentMapper) {
        super(parentMapper, () -> new AccountDis(), () -> new AccountDisDTO());
    }

    @Override
    protected AccountDisDTO localToDTO(AccountDis entity, AccountDisDTO dto) {
        dto.setUsername(entity.getUsername());
        return dto;
    }

    @Override
    protected AccountDis localToEntity(AccountDisDTO dto, AccountDis entity) {
        entity.setUsername(dto.getUsername());
        return entity;
    }
    
}
