package edu.wisc.wud.games.wud_games_website.steam_account;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.account.AccountMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class SteamAccountMapper extends EntityMapper<SteamAccount, SteamAccountDTO> {

    public SteamAccountMapper(AccountMapper parentMapper) {
        super(parentMapper, () -> new SteamAccount(), () -> new SteamAccountDTO());
    }

    @Override
    protected SteamAccountDTO localToDTO(SteamAccount entity, SteamAccountDTO dto) {
        return dto;
    }

    @Override
    protected SteamAccount localToEntity(SteamAccountDTO dto, SteamAccount entity) {
        return entity;
    }
    
}
