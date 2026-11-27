package edu.wisc.wud.games.wud_games_website.board_game;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class BoardGameService extends EntityService<BoardGameRepository, BoardGame, BoardGameDTO> {

    public BoardGameService(BoardGameRepository repository, EntityMapper<BoardGame, BoardGameDTO> mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected BoardGame newEntity() {
        return new BoardGame();
    }

    @Override
    public BoardGameDTO newDTO() {
        return new BoardGameDTO();
    }

}

