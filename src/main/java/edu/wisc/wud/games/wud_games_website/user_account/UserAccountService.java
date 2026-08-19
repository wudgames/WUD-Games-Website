package edu.wisc.wud.games.wud_games_website.user_account;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteTag;
import edu.wisc.wud.games.wud_games_website.tag.TagDTO;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserAccountService {
    
    private final UserAccountRepository userAccountRepository;
    private final ApplicationEventPublisher publisher;

    public UserAccountService(final UserAccountRepository userAccountRepository, final ApplicationEventPublisher publisher) {
        this.userAccountRepository = userAccountRepository;
        this.publisher = publisher;
    }
    /* 
    public List<UserAccountDTO> findAll() {
        final List<UserAccount> userAccounts = userAccountRepository.findAll(Sort.by("id"));
        return userAccounts.stream()
                .map(user -> mapToDTO(user, new UserAccountDTO()))
                .toList();
    }
    */
    public List<UserAccountDTO> findAll() {
        final List<UserAccount> userAccounts = userAccountRepository.findAll(Sort.by("id"));
        return userAccounts.stream()
                .map(userAccount -> mapToDTO(userAccount, new UserAccountDTO()))
                .toList();
    }

    public UserAccountDTO get(final Long id) throws NotFoundException {
        return mapToDTO(
            userAccountRepository.findById(id).orElseThrow(NotFoundException::new),
            new UserAccountDTO());
                
    }

    public Long create(final UserAccountDTO userAccountDTO) {
        final UserAccount userAccount = new UserAccount();
        mapToEntity(userAccountDTO, userAccount);
        return userAccountRepository.save(userAccount).getId();
    }
    /*
    public void update(final Long id, final UserAccountDTO userAccountDTO) {
        final UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userAccountDTO, userAccount);
        userAccountRepository.save(userAccount);
    }

    public void delete(final Long id) {
        final UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteTag(id));
        userAccountRepository.delete(userAccount);
    }
    
    private TagDTO mapToDTO(final Tag tag, final UserAccountDTO tagDTO) {
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        return tagDTO;
    }
    */
    private UserAccount mapToEntity(final UserAccountDTO userAccountDTO, final UserAccount userAccount) {
        userAccount.setId(userAccountDTO.getId());
        userAccount.setEmail(userAccountDTO.getEmail());
        userAccount.setPassword(userAccountDTO.getPassword());
        userAccount.setHost(userAccountDTO.isHost());
        userAccount.setHoursHosted(userAccountDTO.getHoursHosted());
        userAccount.setPhysicalInventoryManager(userAccountDTO.isPhysicalInventoryManager());
        userAccount.setDigitalInventoryManager(userAccountDTO.isDigitalInventoryManager());
        userAccount.setRentalsManager(userAccountDTO.isRentalsManager());
        userAccount.setEventsManager(userAccountDTO.isEventsManager());
        userAccount.setMetaDataManager(userAccountDTO.isMetaDataManager());
        userAccount.setAdmin(userAccountDTO.isAdmin());
        userAccount.setLastLogin(userAccountDTO.getLastLogin());
        return userAccount;
    }
    
    private UserAccountDTO mapToDTO(final UserAccount userAccount, final UserAccountDTO userAccountDTO) {
        userAccountDTO.setId(userAccount.getId());
        userAccountDTO.setEmail(userAccount.getEmail());
        userAccountDTO.setPassword(userAccount.getPassword());
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
