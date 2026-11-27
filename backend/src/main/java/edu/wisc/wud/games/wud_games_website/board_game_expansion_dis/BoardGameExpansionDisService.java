package edu.wisc.wud.games.wud_games_website.board_game_expansion_dis;

import edu.wisc.wud.games.wud_games_website.events.before_delete.BeforeDeleteBoardGameDis;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import edu.wisc.wud.games.wud_games_website.util.ReferencedException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class BoardGameExpansionDisService extends EntityService<BoardGameExpansionDisRepository, BoardGameExpansionDis, BoardGameExpansionDisDTO> {
    public BoardGameExpansionDisService(BoardGameExpansionDisRepository repository,
            BoardGameExpansionDisMapper mapper,
            final ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
        // this.boardGameDisService = boardGameDisService;
    }
    /*
     * public Map<Long, Long> getBoardGameDisValues() {
     * return boardGameExpansionDisRepository.findAll(Sort.by("id"))
     * .stream()
     * .collect(CustomCollectors.toSortedMap(BoardGameExpansionDis::getId,
     * BoardGameExpansionDis::getId));
     * }
     */

    @Override
    protected BoardGameExpansionDis newEntity() {
        return new BoardGameExpansionDis();
    }

    @Override
    public BoardGameExpansionDisDTO newDTO() {
        return new BoardGameExpansionDisDTO();
    }

    @EventListener(BeforeDeleteBoardGameDis.class)
    public void on(final BeforeDeleteBoardGameDis event) {
        final ReferencedException referencedException = new ReferencedException();
        final BoardGameExpansionDis boardGameExpansionDis = repository.findFirstByBaseBoardGameDisId(event.getId());
        if (boardGameExpansionDis != null) {
            referencedException.setKey("boardGameDis.boardGame.boardGameDis.referenced");
            referencedException.addParam(boardGameExpansionDis.getId());
            throw referencedException;
        }
    }
}
