package edu.wisc.wud.games.wud_games_website.user_account;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserAccountService extends EntityService<UserAccountRepository, UserAccount, UserAccountDTO> {
    
    public UserAccountService(UserAccountRepository repository, UserAccountMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    public UserAccountDTO findByEmail(String email) {
        return mapper.toDTO(repository.findByEmail(email));
    }

    public List<UserAccountDTO> emailContains(String email) {
        if (email == null || email.isBlank()) {
            return findAll();
        }
        return mapper.allToDTO(repository.emailContains(email));
    }

    @Override
    protected UserAccount newEntity() {
        return new UserAccount();
    }

    @Override
    public UserAccountDTO newDTO() {
        return new UserAccountDTO();
    }
}
