package edu.wisc.wud.games.wud_games_website.board_game_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BoardGameDisService {

    private final BoardGameDisRepository boardGameDisRepository;
    private final ApplicationEventPublisher publisher;

    public BoardGameDisService(final BoardGameDisRepository boardGameDisRepository,
            final ApplicationEventPublisher publisher) {
        this.boardGameDisRepository = boardGameDisRepository;
        this.publisher = publisher;
    }

    public List<BoardGameDisDTO> findAll() {
        final List<BoardGameDis> boardGameDises = boardGameDisRepository.findAll(Sort.by("id"));
        return boardGameDises.stream()
                .map(boardGameDis -> mapToDTO(boardGameDis, new BoardGameDisDTO()))
                .toList();
    }

    public BoardGameDisDTO get(final Long id) {
        return boardGameDisRepository.findById(id)
                .map(boardGameDis -> mapToDTO(boardGameDis, new BoardGameDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BoardGameDisDTO boardGameDisDTO) {
        final BoardGameDis boardGameDis = new BoardGameDis();
        mapToEntity(boardGameDisDTO, boardGameDis);
        return boardGameDisRepository.save(boardGameDis).getId();
    }

    public void update(final Long id, final BoardGameDisDTO boardGameDisDTO) {
        final BoardGameDis boardGameDis = boardGameDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(boardGameDisDTO, boardGameDis);
        boardGameDisRepository.save(boardGameDis);
    }

    public void delete(final Long id) {
        final BoardGameDis boardGameDis = boardGameDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteBoardGameDis(id));
        boardGameDisRepository.delete(boardGameDis);
    }

    private BoardGameDisDTO mapToDTO(final BoardGameDis boardGameDis,
            final BoardGameDisDTO boardGameDisDTO) {
        boardGameDisDTO.setId(boardGameDis.getId());
        boardGameDisDTO.setMinPlaytime(boardGameDis.getMinPlaytime());
        boardGameDisDTO.setMaxPlaytime(boardGameDis.getMaxPlaytime());
        return boardGameDisDTO;
    }

    private BoardGameDis mapToEntity(final BoardGameDisDTO boardGameDisDTO,
            final BoardGameDis boardGameDis) {
        boardGameDis.setMinPlaytime(boardGameDisDTO.getMinPlaytime());
        boardGameDis.setMaxPlaytime(boardGameDisDTO.getMaxPlaytime());
        return boardGameDis;
    }

    public Map<Long, Long> getBoardGameDisValues() {
        return boardGameDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(BoardGameDis::getId, BoardGameDis::getId));
    }

}

