package edu.wisc.wud.games.wud_games_website.account;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class AccountService extends EntityService<AccountRepository, Account, AccountDTO> {

    public AccountService(AccountRepository repository, AccountMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected Account newEntity() {
        return new Account();
    }

    @Override
    public AccountDTO newDTO() {
        return new AccountDTO();
    }

    public Map<Long, Long> getAccountValues() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Account::getId, Account::getId));
    }
}

