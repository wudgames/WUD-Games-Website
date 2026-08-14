package edu.wisc.wud.games.wud_games_website.oauth2.user_account;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
    
    private final UserAccountRepository userAccountRepository;
    private final ApplicationEventPublisher publisher;

    public UserAccountService(final UserAccountRepository userAccountRepository, final ApplicationEventPublisher publisher) {
        this.userAccountRepository = userAccountRepository;
        this.publisher = publisher;
    }

    public List<UserAccountDTO> findAll() {
        final List<UserAccount> userAccounts = userAccountRepository.findAll(Sort.by("id"));
        return userAccounts.stream()
                .map(user -> mapToDTO(user, new UserAccountDTO()))
                .toList();
    }

    private UserAccountDTO mapToDTO(final UserAccount userAccount, final UserAccountDTO userAccountDTO) {
        userAccountDTO.setId(userAccount.getId());
        userAccountDTO.setEmail(userAccount.getEmail());
        //userDTO.setPassword(user.getPassword());
        userAccountDTO.setHost(userAccount.isHost());
        userAccountDTO.setHoursHosted(userAccount.getHoursHosted());
        userAccountDTO.setPhysicalInventoryManager(userAccount.isPhysicalInventoryManager());
        userAccountDTO.setDigitalInventoryManager(userAccount.isDigitalInventoryManager());
        userAccountDTO.setRentalsManager(userAccount.isRentalsManager());
        userAccountDTO.setEventsManager(userAccount.isEventsManager());
        userAccountDTO.setMetaDataManager(userAccount.isMetaDataManager());
        userAccountDTO.setAdmin(userAccount.isAdmin());
        userAccountDTO.setLastLogin(userAccount.getLastLogin());
        return userAccountDTO;
    }
}
