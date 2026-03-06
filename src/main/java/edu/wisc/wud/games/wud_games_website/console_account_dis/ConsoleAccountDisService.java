package edu.wisc.wud.games.wud_games_website.console_account_dis;

import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ConsoleAccountDisService {

    private final ConsoleAccountDisRepository consoleAccountDisRepository;

    public ConsoleAccountDisService(final ConsoleAccountDisRepository consoleAccountDisRepository) {
        this.consoleAccountDisRepository = consoleAccountDisRepository;
    }

    public List<ConsoleAccountDisDTO> findAll() {
        final List<ConsoleAccountDis> consoleAccountDises = consoleAccountDisRepository.findAll(Sort.by("id"));
        return consoleAccountDises.stream()
                .map(consoleAccountDis -> mapToDTO(consoleAccountDis, new ConsoleAccountDisDTO()))
                .toList();
    }

    public ConsoleAccountDisDTO get(final Long id) {
        return consoleAccountDisRepository.findById(id)
                .map(consoleAccountDis -> mapToDTO(consoleAccountDis, new ConsoleAccountDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ConsoleAccountDisDTO consoleAccountDisDTO) {
        final ConsoleAccountDis consoleAccountDis = new ConsoleAccountDis();
        mapToEntity(consoleAccountDisDTO, consoleAccountDis);
        return consoleAccountDisRepository.save(consoleAccountDis).getId();
    }

    public void update(final Long id, final ConsoleAccountDisDTO consoleAccountDisDTO) {
        final ConsoleAccountDis consoleAccountDis = consoleAccountDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(consoleAccountDisDTO, consoleAccountDis);
        consoleAccountDisRepository.save(consoleAccountDis);
    }

    public void delete(final Long id) {
        final ConsoleAccountDis consoleAccountDis = consoleAccountDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        consoleAccountDisRepository.delete(consoleAccountDis);
    }

    private ConsoleAccountDisDTO mapToDTO(final ConsoleAccountDis consoleAccountDis,
            final ConsoleAccountDisDTO consoleAccountDisDTO) {
        consoleAccountDisDTO.setId(consoleAccountDis.getId());
        return consoleAccountDisDTO;
    }

    private ConsoleAccountDis mapToEntity(final ConsoleAccountDisDTO consoleAccountDisDTO,
            final ConsoleAccountDis consoleAccountDis) {
        return consoleAccountDis;
    }

}

