package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteGameExpansionDis;
import edu.wisc.wud.games.wud_games_website.game_expansion_dis.GameExpansionDis;
import edu.wisc.wud.games.wud_games_website.game_expansion_dis.GameExpansionDisRepository;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BoardGameExpansionService {

    private final BoardGameExpansionRepository boardGameExpansionRepository;
    private final GameExpansionDisRepository gameExpansionDisRepository;

    public BoardGameExpansionService(
            final BoardGameExpansionRepository boardGameExpansionRepository,
            final GameExpansionDisRepository gameExpansionDisRepository) {
        this.boardGameExpansionRepository = boardGameExpansionRepository;
        this.gameExpansionDisRepository = gameExpansionDisRepository;
    }

    public List<BoardGameExpansionDTO> findAll() {
        final List<BoardGameExpansion> boardGameExpansions = boardGameExpansionRepository.findAll(Sort.by("id"));
        return boardGameExpansions.stream()
                .map(boardGameExpansion -> mapToDTO(boardGameExpansion, new BoardGameExpansionDTO()))
                .toList();
    }

    public BoardGameExpansionDTO get(final Long id) {
        return boardGameExpansionRepository.findById(id)
                .map(boardGameExpansion -> mapToDTO(boardGameExpansion, new BoardGameExpansionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BoardGameExpansionDTO boardGameExpansionDTO) {
        final BoardGameExpansion boardGameExpansion = new BoardGameExpansion();
        mapToEntity(boardGameExpansionDTO, boardGameExpansion);
        return boardGameExpansionRepository.save(boardGameExpansion).getId();
    }

    public void update(final Long id, final BoardGameExpansionDTO boardGameExpansionDTO) {
        final BoardGameExpansion boardGameExpansion = boardGameExpansionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(boardGameExpansionDTO, boardGameExpansion);
        boardGameExpansionRepository.save(boardGameExpansion);
    }

    public void delete(final Long id) {
        final BoardGameExpansion boardGameExpansion = boardGameExpansionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        boardGameExpansionRepository.delete(boardGameExpansion);
    }

    private BoardGameExpansionDTO mapToDTO(final BoardGameExpansion boardGameExpansion,
            final BoardGameExpansionDTO boardGameExpansionDTO) {
        boardGameExpansionDTO.setId(boardGameExpansion.getId());
        boardGameExpansionDTO.setGameExpansionDis(boardGameExpansion.getGameExpansionDis() == null ? null : boardGameExpansion.getGameExpansionDis().getId());
        return boardGameExpansionDTO;
    }

    private BoardGameExpansion mapToEntity(final BoardGameExpansionDTO boardGameExpansionDTO,
            final BoardGameExpansion boardGameExpansion) {
        final GameExpansionDis gameExpansionDis = boardGameExpansionDTO.getGameExpansionDis() == null ? null : gameExpansionDisRepository.findById(boardGameExpansionDTO.getGameExpansionDis())
                .orElseThrow(() -> new NotFoundException("gameExpansionDis not found"));
        boardGameExpansion.setGameExpansionDis(gameExpansionDis);
        return boardGameExpansion;
    }

    @EventListener(BeforeDeleteGameExpansionDis.class)
    public void on(final BeforeDeleteGameExpansionDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final BoardGameExpansion gameExpansionDisBoardGameExpansion = boardGameExpansionRepository.findFirstByGameExpansionDisId(event.getId());
        if (gameExpansionDisBoardGameExpansion != null) {
            referencedException.setKey("gameExpansionDis.boardGameExpansion.gameExpansionDis.referenced");
            referencedException.addParam(gameExpansionDisBoardGameExpansion.getId());
            throw referencedException;
        }
    }

}

