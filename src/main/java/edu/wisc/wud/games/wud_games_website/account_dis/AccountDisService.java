package edu.wisc.wud.games.wud_games_website.account_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccountDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AccountDisService {

    private final AccountDisRepository accountDisRepository;
    private final ApplicationEventPublisher publisher;

    public AccountDisService(final AccountDisRepository accountDisRepository,
            final ApplicationEventPublisher publisher) {
        this.accountDisRepository = accountDisRepository;
        this.publisher = publisher;
    }

    public List<AccountDisDTO> findAll() {
        final List<AccountDis> accountDises = accountDisRepository.findAll(Sort.by("id"));
        return accountDises.stream()
                .map(accountDis -> mapToDTO(accountDis, new AccountDisDTO()))
                .toList();
    }

    public AccountDisDTO get(final Long id) {
        return accountDisRepository.findById(id)
                .map(accountDis -> mapToDTO(accountDis, new AccountDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AccountDisDTO accountDisDTO) {
        final AccountDis accountDis = new AccountDis();
        mapToEntity(accountDisDTO, accountDis);
        return accountDisRepository.save(accountDis).getId();
    }

    public void update(final Long id, final AccountDisDTO accountDisDTO) {
        final AccountDis accountDis = accountDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(accountDisDTO, accountDis);
        accountDisRepository.save(accountDis);
    }

    public void delete(final Long id) {
        final AccountDis accountDis = accountDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteAccountDis(id));
        accountDisRepository.delete(accountDis);
    }

    private AccountDisDTO mapToDTO(final AccountDis accountDis, final AccountDisDTO accountDisDTO) {
        accountDisDTO.setId(accountDis.getId());
        accountDisDTO.setUsername(accountDis.getUsername());
        accountDisDTO.setAvalible(accountDis.getAvalible());
        accountDisDTO.setNotes(accountDis.getNotes());
        return accountDisDTO;
    }

    private AccountDis mapToEntity(final AccountDisDTO accountDisDTO, final AccountDis accountDis) {
        accountDis.setUsername(accountDisDTO.getUsername());
        accountDis.setAvalible(accountDisDTO.getAvalible());
        accountDis.setNotes(accountDisDTO.getNotes());
        return accountDis;
    }

    public Map<Long, String> getAccountDisValues() {
        return accountDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(AccountDis::getId, AccountDis::getUsername));
    }

}

