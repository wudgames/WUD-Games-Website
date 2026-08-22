package edu.wisc.wud.games.wud_games_website.board_game_expansion;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
public class BoardGameExpansionService extends EntityService<BoardGameExpansionRepository, BoardGameExpansion, BoardGameExpansionDTO>  {

    public BoardGameExpansionService(BoardGameExpansionRepository repository,
            EntityMapper<BoardGameExpansion, BoardGameExpansionDTO> mapper, ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    @Override
    protected BoardGameExpansion newEntity() {
        return new BoardGameExpansion();
    }

    @Override
    public BoardGameExpansionDTO newDTO() {
        return new BoardGameExpansionDTO();
    }

}

