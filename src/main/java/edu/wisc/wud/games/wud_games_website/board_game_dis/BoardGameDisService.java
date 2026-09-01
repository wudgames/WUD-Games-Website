package edu.wisc.wud.games.wud_games_website.board_game_dis;

import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.general_dis.EntityService;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service("BoardGameDisService")
public class BoardGameDisService extends EntityService<BoardGameDisRepository, BoardGameDis, BoardGameDisDTO> {

    public BoardGameDisService(BoardGameDisRepository repository, BoardGameDisMapper mapper,
            ApplicationEventPublisher publisher) {
        super(repository, mapper, publisher);
    }

    public Map<Long, Long> getBoardGameDisValues() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(BoardGameDis::getId, BoardGameDis::getId));
    }

    public BoardGameDisDTO findByName(String name) {
        return mapper.toDTO(repository.findByName(name));
    }

    @Override
    protected BoardGameDis newEntity() {
        return new BoardGameDis();
    }

    @Override
    public BoardGameDisDTO newDTO() {
        return new BoardGameDisDTO();
    }

}

