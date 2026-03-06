package edu.wisc.wud.games.wud_games_website.board_game;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BoardGameService {

    private final BoardGameRepository boardGameRepository;
    private final BoardGameDisRepository boardGameDisRepository;

    public BoardGameService(final BoardGameRepository boardGameRepository,
            final BoardGameDisRepository boardGameDisRepository) {
        this.boardGameRepository = boardGameRepository;
        this.boardGameDisRepository = boardGameDisRepository;
    }

    public List<BoardGameDTO> findAll() {
        final List<BoardGame> boardGames = boardGameRepository.findAll(Sort.by("id"));
        return boardGames.stream()
                .map(boardGame -> mapToDTO(boardGame, new BoardGameDTO()))
                .toList();
    }

    public BoardGameDTO get(final Long id) {
        return boardGameRepository.findById(id)
                .map(boardGame -> mapToDTO(boardGame, new BoardGameDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BoardGameDTO boardGameDTO) {
        final BoardGame boardGame = new BoardGame();
        mapToEntity(boardGameDTO, boardGame);
        return boardGameRepository.save(boardGame).getId();
    }

    public void update(final Long id, final BoardGameDTO boardGameDTO) {
        final BoardGame boardGame = boardGameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(boardGameDTO, boardGame);
        boardGameRepository.save(boardGame);
    }

    public void delete(final Long id) {
        final BoardGame boardGame = boardGameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        boardGameRepository.delete(boardGame);
    }

    private BoardGameDTO mapToDTO(final BoardGame boardGame, final BoardGameDTO boardGameDTO) {
        boardGameDTO.setId(boardGame.getId());
        boardGameDTO.setMinPlaytime(boardGame.getMinPlaytime());
        boardGameDTO.setMaxPlaytime(boardGame.getMaxPlaytime());
        boardGameDTO.setBoardGameDis(boardGame.getBoardGameDis() == null ? null : boardGame.getBoardGameDis().getId());
        return boardGameDTO;
    }

    private BoardGame mapToEntity(final BoardGameDTO boardGameDTO, final BoardGame boardGame) {
        boardGame.setMinPlaytime(boardGameDTO.getMinPlaytime());
        boardGame.setMaxPlaytime(boardGameDTO.getMaxPlaytime());
        final BoardGameDis boardGameDis = boardGameDTO.getBoardGameDis() == null ? null : boardGameDisRepository.findById(boardGameDTO.getBoardGameDis())
                .orElseThrow(() -> new NotFoundException("boardGameDis not found"));
        boardGame.setBoardGameDis(boardGameDis);
        return boardGame;
    }

    @EventListener(BeforeDeleteBoardGameDis.class)
    public void on(final BeforeDeleteBoardGameDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final BoardGame boardGameDisBoardGame = boardGameRepository.findFirstByBoardGameDisId(event.getId());
        if (boardGameDisBoardGame != null) {
            referencedException.setKey("boardGameDis.boardGame.boardGameDis.referenced");
            referencedException.addParam(boardGameDisBoardGame.getId());
            throw referencedException;
        }
    }

}

