package edu.wisc.wud.games.wud_games_website.digital_item;

import edu.wisc.wud.games.wud_games_website.account.Account;
import edu.wisc.wud.games.wud_games_website.account.AccountRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteAccount;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.HashSet;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class DigitalItemService {

    private final DigitalItemRepository digitalItemRepository;
    private final AccountRepository accountRepository;

    public DigitalItemService(final DigitalItemRepository digitalItemRepository,
            final AccountRepository accountRepository) {
        this.digitalItemRepository = digitalItemRepository;
        this.accountRepository = accountRepository;
    }

    public List<DigitalItemDTO> findAll() {
        final List<DigitalItem> digitalItems = digitalItemRepository.findAll(Sort.by("id"));
        return digitalItems.stream()
                .map(digitalItem -> mapToDTO(digitalItem, new DigitalItemDTO()))
                .toList();
    }

    public DigitalItemDTO get(final Long id) {
        return digitalItemRepository.findById(id)
                .map(digitalItem -> mapToDTO(digitalItem, new DigitalItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DigitalItemDTO digitalItemDTO) {
        final DigitalItem digitalItem = new DigitalItem();
        mapToEntity(digitalItemDTO, digitalItem);
        return digitalItemRepository.save(digitalItem).getId();
    }

    public void update(final Long id, final DigitalItemDTO digitalItemDTO) {
        final DigitalItem digitalItem = digitalItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(digitalItemDTO, digitalItem);
        digitalItemRepository.save(digitalItem);
    }

    public void delete(final Long id) {
        final DigitalItem digitalItem = digitalItemRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        digitalItemRepository.delete(digitalItem);
    }

    private DigitalItemDTO mapToDTO(final DigitalItem digitalItem,
            final DigitalItemDTO digitalItemDTO) {
        digitalItemDTO.setId(digitalItem.getId());
        digitalItemDTO.setCompatableAccounts(digitalItem.getCompatableAccounts().stream()
                .map(account -> account.getId())
                .toList());
        return digitalItemDTO;
    }

    private DigitalItem mapToEntity(final DigitalItemDTO digitalItemDTO,
            final DigitalItem digitalItem) {
        final List<Account> compatableAccounts = accountRepository.findAllById(
                digitalItemDTO.getCompatableAccounts() == null ? List.of() : digitalItemDTO.getCompatableAccounts());
        if (compatableAccounts.size() != (digitalItemDTO.getCompatableAccounts() == null ? 0 : digitalItemDTO.getCompatableAccounts().size())) {
            throw new NotFoundException("one of compatableAccounts not found");
        }
        digitalItem.setCompatableAccounts(new HashSet<>(compatableAccounts));
        return digitalItem;
    }

    @EventListener(BeforeDeleteAccount.class)
    public void on(final BeforeDeleteAccount event) {
        // remove many-to-many relations at owning side
        digitalItemRepository.findAllByCompatableAccountsId(event.getId()).forEach(digitalItem ->
                digitalItem.getCompatableAccounts().removeIf(account -> account.getId().equals(event.getId())));
    }

}

