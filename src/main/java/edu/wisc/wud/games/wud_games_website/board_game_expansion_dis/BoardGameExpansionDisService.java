package edu.wisc.wud.games.wud_games_website.board_game_expansion_dis;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import edu.wisc.wud.games.wud_games_website.board_game.BoardGame;
import edu.wisc.wud.games.wud_games_website.board_game.BoardGameDTO;
import edu.wisc.wud.games.wud_games_website.board_game.BoardGameRepository;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDis;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisRepository;
import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisService;
import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;

@Service("BoardGameExpansionDisService")
public class BoardGameExpansionDisService extends BoardGameDisService {
    
    private final BoardGameExpansionDisRepository boardGameExpansionDisRepository;
    private final BoardGameDisRepository boardGameDisRepository;

    public BoardGameExpansionDisService(final BoardGameExpansionDisRepository boardGameExpansionDisRepository, BoardGameDisRepository boardGameDisRepository) {
        this.boardGameExpansionDisRepository = boardGameExpansionDisRepository;
        this.boardGameDisRepository = boardGameDisRepository;
    }
    /*
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
    */
    public Long create(final BoardGameExpansionDisDTO boardGameExpansionDisDTO) {
        final BoardGameExpansionDis boardGameExpansionDis = new BoardGameExpansionDis();
        mapToEntity(boardGameExpansionDisDTO, boardGameExpansionDis);
        return boardGameExpansionDisRepository.save(boardGameExpansionDis).getId();
    }
    /*
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
    
    private BoardGameDTO mapToDTO(final BoardGameExpansionDis boardGameExpansionDis, final BoardGameExpansionDisDTO boardGameExpansionDisDTO) {
        super.mapToDTO(boardGameExpansionDis, boardGameExpansionDisDTO);
        boardGameDTO.setId(boardGame.getId());
        boardGameDTO.setMinPlaytime(boardGame.getMinPlaytime());
        boardGameDTO.setMaxPlaytime(boardGame.getMaxPlaytime());
        //boardGameDTO.setBoardGameDis(boardGame.getBoardGameDis() == null ? null : boardGame.getBoardGameDis().getId());
        boardGameExpansionDis.setBaseBoardGameDis(boardGameExpansionDis);
        return boardGameDTO;
    }
    */
    private BoardGameExpansionDis mapToEntity(final BoardGameExpansionDisDTO boardGameExpansionDisDTO, final BoardGameExpansionDis boardGameExpansionDis) {
        super.mapToEntity(boardGameExpansionDisDTO, boardGameExpansionDis);
        final BoardGameDis boardGameDis = boardGameExpansionDisDTO.getBaseGameDis() == null ? null : boardGameDisRepository.findById(boardGameExpansionDisDTO.getBaseGameDis())
                .orElseThrow(() -> new NotFoundException("boardGameDis not found"));
        boardGameExpansionDis.setBaseBoardGameDis(boardGameDis);
        return boardGameExpansionDis;
    }
    /*
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
    */
}
