package edu.wisc.wud.games.wud_games_website.steam_account_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteSteamAccountDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SteamAccountDisService {

    private final SteamAccountDisRepository steamAccountDisRepository;
    private final ApplicationEventPublisher publisher;

    public SteamAccountDisService(final SteamAccountDisRepository steamAccountDisRepository,
            final ApplicationEventPublisher publisher) {
        this.steamAccountDisRepository = steamAccountDisRepository;
        this.publisher = publisher;
    }

    public List<SteamAccountDisDTO> findAll() {
        final List<SteamAccountDis> steamAccountDises = steamAccountDisRepository.findAll(Sort.by("id"));
        return steamAccountDises.stream()
                .map(steamAccountDis -> mapToDTO(steamAccountDis, new SteamAccountDisDTO()))
                .toList();
    }

    public SteamAccountDisDTO get(final Long id) {
        return steamAccountDisRepository.findById(id)
                .map(steamAccountDis -> mapToDTO(steamAccountDis, new SteamAccountDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SteamAccountDisDTO steamAccountDisDTO) {
        final SteamAccountDis steamAccountDis = new SteamAccountDis();
        mapToEntity(steamAccountDisDTO, steamAccountDis);
        return steamAccountDisRepository.save(steamAccountDis).getId();
    }

    public void update(final Long id, final SteamAccountDisDTO steamAccountDisDTO) {
        final SteamAccountDis steamAccountDis = steamAccountDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(steamAccountDisDTO, steamAccountDis);
        steamAccountDisRepository.save(steamAccountDis);
    }

    public void delete(final Long id) {
        final SteamAccountDis steamAccountDis = steamAccountDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSteamAccountDis(id));
        steamAccountDisRepository.delete(steamAccountDis);
    }

    private SteamAccountDisDTO mapToDTO(final SteamAccountDis steamAccountDis,
            final SteamAccountDisDTO steamAccountDisDTO) {
        steamAccountDisDTO.setId(steamAccountDis.getId());
        return steamAccountDisDTO;
    }

    private SteamAccountDis mapToEntity(final SteamAccountDisDTO steamAccountDisDTO,
            final SteamAccountDis steamAccountDis) {
        return steamAccountDis;
    }

    public Map<Long, Long> getSteamAccountDisValues() {
        return steamAccountDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(SteamAccountDis::getId, SteamAccountDis::getId));
    }

}

