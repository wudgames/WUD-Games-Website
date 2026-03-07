package edu.wisc.wud.games.wud_games_website.account;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDis;
import edu.wisc.wud.games.wud_games_website.account_dis.AccountDisRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccount;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccountDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountDisRepository accountDisRepository;
    private final ApplicationEventPublisher publisher;

    public AccountService(final AccountRepository accountRepository,
            final AccountDisRepository accountDisRepository,
            final ApplicationEventPublisher publisher) {
        this.accountRepository = accountRepository;
        this.accountDisRepository = accountDisRepository;
        this.publisher = publisher;
    }

    public List<AccountDTO> findAll() {
        final List<Account> accounts = accountRepository.findAll(Sort.by("id"));
        return accounts.stream()
                .map(account -> mapToDTO(account, new AccountDTO()))
                .toList();
    }

    public AccountDTO get(final Long id) {
        return accountRepository.findById(id)
                .map(account -> mapToDTO(account, new AccountDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AccountDTO accountDTO) {
        final Account account = new Account();
        mapToEntity(accountDTO, account);
        return accountRepository.save(account).getId();
    }

    public void update(final Long id, final AccountDTO accountDTO) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(accountDTO, account);
        accountRepository.save(account);
    }

    public void delete(final Long id) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteAccount(id));
        accountRepository.delete(account);
    }

    private AccountDTO mapToDTO(final Account account, final AccountDTO accountDTO) {
        accountDTO.setId(account.getId());
        accountDTO.setAccountDis(account.getAccountDis() == null ? null : account.getAccountDis().getId());
        return accountDTO;
    }

    private Account mapToEntity(final AccountDTO accountDTO, final Account account) {
        final AccountDis accountDis = accountDTO.getAccountDis() == null ? null : accountDisRepository.findById(accountDTO.getAccountDis())
                .orElseThrow(() -> new NotFoundException("accountDis not found"));
        account.setAccountDis(accountDis);
        return account;
    }

    public Map<Long, Long> getAccountValues() {
        return accountRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Account::getId, Account::getId));
    }

    @EventListener(BeforeDeleteAccountDis.class)
    public void on(final BeforeDeleteAccountDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final Account accountDisAccount = accountRepository.findFirstByAccountDisId(event.getId());
        if (accountDisAccount != null) {
            referencedException.setKey("accountDis.account.accountDis.referenced");
            referencedException.addParam(accountDisAccount.getId());
            throw referencedException;
        }
    }

}

