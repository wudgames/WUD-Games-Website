package edu.wisc.wud.games.wud_games_website.console_account;

import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ConsoleAccountService {

    private final ConsoleAccountRepository consoleAccountRepository;

    public ConsoleAccountService(final ConsoleAccountRepository consoleAccountRepository) {
        this.consoleAccountRepository = consoleAccountRepository;
    }

    public List<ConsoleAccountDTO> findAll() {
        final List<ConsoleAccount> consoleAccounts = consoleAccountRepository.findAll(Sort.by("id"));
        return consoleAccounts.stream()
                .map(consoleAccount -> mapToDTO(consoleAccount, new ConsoleAccountDTO()))
                .toList();
    }

    public ConsoleAccountDTO get(final Long id) {
        return consoleAccountRepository.findById(id)
                .map(consoleAccount -> mapToDTO(consoleAccount, new ConsoleAccountDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ConsoleAccountDTO consoleAccountDTO) {
        final ConsoleAccount consoleAccount = new ConsoleAccount();
        mapToEntity(consoleAccountDTO, consoleAccount);
        return consoleAccountRepository.save(consoleAccount).getId();
    }

    public void update(final Long id, final ConsoleAccountDTO consoleAccountDTO) {
        final ConsoleAccount consoleAccount = consoleAccountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(consoleAccountDTO, consoleAccount);
        consoleAccountRepository.save(consoleAccount);
    }

    public void delete(final Long id) {
        final ConsoleAccount consoleAccount = consoleAccountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        consoleAccountRepository.delete(consoleAccount);
    }

    private ConsoleAccountDTO mapToDTO(final ConsoleAccount consoleAccount,
            final ConsoleAccountDTO consoleAccountDTO) {
        consoleAccountDTO.setId(consoleAccount.getId());
        return consoleAccountDTO;
    }

    private ConsoleAccount mapToEntity(final ConsoleAccountDTO consoleAccountDTO,
            final ConsoleAccount consoleAccount) {
        return consoleAccount;
    }

}

