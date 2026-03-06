package edu.wisc.wud.games.wud_games_website.steam_account;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteSteamAccountDis;
import edu.wisc.wud.games.wud_games_website.steam_account_dis.SteamAccountDis;
import edu.wisc.wud.games.wud_games_website.steam_account_dis.SteamAccountDisRepository;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SteamAccountService {

    private final SteamAccountRepository steamAccountRepository;
    private final SteamAccountDisRepository steamAccountDisRepository;

    public SteamAccountService(final SteamAccountRepository steamAccountRepository,
            final SteamAccountDisRepository steamAccountDisRepository) {
        this.steamAccountRepository = steamAccountRepository;
        this.steamAccountDisRepository = steamAccountDisRepository;
    }

    public List<SteamAccountDTO> findAll() {
        final List<SteamAccount> steamAccounts = steamAccountRepository.findAll(Sort.by("id"));
        return steamAccounts.stream()
                .map(steamAccount -> mapToDTO(steamAccount, new SteamAccountDTO()))
                .toList();
    }

    public SteamAccountDTO get(final Long id) {
        return steamAccountRepository.findById(id)
                .map(steamAccount -> mapToDTO(steamAccount, new SteamAccountDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SteamAccountDTO steamAccountDTO) {
        final SteamAccount steamAccount = new SteamAccount();
        mapToEntity(steamAccountDTO, steamAccount);
        return steamAccountRepository.save(steamAccount).getId();
    }

    public void update(final Long id, final SteamAccountDTO steamAccountDTO) {
        final SteamAccount steamAccount = steamAccountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(steamAccountDTO, steamAccount);
        steamAccountRepository.save(steamAccount);
    }

    public void delete(final Long id) {
        final SteamAccount steamAccount = steamAccountRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        steamAccountRepository.delete(steamAccount);
    }

    private SteamAccountDTO mapToDTO(final SteamAccount steamAccount,
            final SteamAccountDTO steamAccountDTO) {
        steamAccountDTO.setId(steamAccount.getId());
        steamAccountDTO.setSteamAccountDis(steamAccount.getSteamAccountDis() == null ? null : steamAccount.getSteamAccountDis().getId());
        return steamAccountDTO;
    }

    private SteamAccount mapToEntity(final SteamAccountDTO steamAccountDTO,
            final SteamAccount steamAccount) {
        final SteamAccountDis steamAccountDis = steamAccountDTO.getSteamAccountDis() == null ? null : steamAccountDisRepository.findById(steamAccountDTO.getSteamAccountDis())
                .orElseThrow(() -> new NotFoundException("steamAccountDis not found"));
        steamAccount.setSteamAccountDis(steamAccountDis);
        return steamAccount;
    }

    @EventListener(BeforeDeleteSteamAccountDis.class)
    public void on(final BeforeDeleteSteamAccountDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final SteamAccount steamAccountDisSteamAccount = steamAccountRepository.findFirstBySteamAccountDisId(event.getId());
        if (steamAccountDisSteamAccount != null) {
            referencedException.setKey("steamAccountDis.steamAccount.steamAccountDis.referenced");
            referencedException.addParam(steamAccountDisSteamAccount.getId());
            throw referencedException;
        }
    }

}

