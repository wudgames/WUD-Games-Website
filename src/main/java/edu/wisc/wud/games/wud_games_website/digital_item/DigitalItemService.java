package edu.wisc.wud.games.wud_games_website.digital_item;

import edu.wisc.wud.games.wud_games_website.account_dis.AccountDis;
import edu.wisc.wud.games.wud_games_website.account_dis.AccountDisRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDelete;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccountDis;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class DigitalItemService extends EntityService<DigitalItemRepository, DigitalItem, DigitalItemDTO> {
    private final AccountDisRepository accountDisRepository;

    public DigitalItemService(DigitalItemRepository repository, DigitalItemMapper mapper,
            ApplicationEventPublisher publisher, AccountDisRepository accountDisRepository) {
        super(repository, mapper, publisher);
        this.accountDisRepository = accountDisRepository;
    }

    @Override
    protected DigitalItem newEntity() {
        return new DigitalItem();
    }

    @Override
    public DigitalItemDTO newDTO() {
        return new DigitalItemDTO();
    }

    @EventListener(BeforeDeleteAccountDis.class)
    public void on(final BeforeDeleteAccountDis event) {
        // remove many-to-many relations at owning side
        AccountDis accountDeleted = accountDisRepository.findById(event.getId()).orElseThrow();
        repository.findAllByCompatibleAccountsContaining(accountDeleted).forEach(digitalItem ->
                digitalItem.getCompatibleAccounts().removeIf(account -> account.getId().equals(event.getId())));
    }
}

