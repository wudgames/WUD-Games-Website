package edu.wisc.wud.games.wud_games_website.steam_account;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
public class SteamAccountService extends EntityService<SteamAccountRepository, SteamAccount, SteamAccountDTO> {

    public SteamAccountService(SteamAccountRepository repository, SteamAccountMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected SteamAccount newEntity() {
        return new SteamAccount();
    }

    @Override
    public SteamAccountDTO newDTO() {
        return new SteamAccountDTO();
    }

}

