package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.board_game_dis.BoardGameDisDTO;
import edu.wisc.wud.games.wud_games_website.tag.TagDTO;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardGameSearchRankingTest {
    @Test
    void ranksExactNameAndMatchingCharacteristicsAheadOfRelatedGames() {
        BoardGameDisDTO unrelated = game("Azul", "pattern building", 2, 4, "abstract");
        BoardGameDisDTO related = game("Pandemic", "cooperative strategy", 2, 4, "cooperative, strategy");
        BoardGameDisDTO closest = game("Strategy Quest", "cooperative strategy", 2, 4, "cooperative, strategy");

        List<GeneralDisDTO> ranked = BoardGameSearchRanking.rank(
                List.of(unrelated, related, closest), "Strategy Quest cooperative 4 players");

        assertEquals(List.of("Strategy Quest", "Pandemic"), ranked.stream().map(GeneralDisDTO::getName).toList());
    }

    @Test
    void matchesSearchTermsAgainstGameCharacteristicsAndPlayerRange() {
        BoardGameDisDTO fourPlayerGame = game("Island Rescue", "team adventure", 3, 5, "cooperative, adventure");
        BoardGameDisDTO twoPlayerGame = game("Solo Tactics", "team adventure", 1, 2, "cooperative, adventure");

        List<GeneralDisDTO> ranked = BoardGameSearchRanking.rank(
                List.of(twoPlayerGame, fourPlayerGame), "cooperative adventure for 4 players");

        assertEquals(List.of("Island Rescue", "Solo Tactics"),
                ranked.stream().map(GeneralDisDTO::getName).toList());
    }

    @Test
    void omitsGamesWithNoMatchingCharacteristics() {
        BoardGameDisDTO game = game("Azul", "pattern building", 2, 4, "abstract");

        assertEquals(List.of(), BoardGameSearchRanking.rank(List.of(game), "cooperative strategy"));
    }

    private static BoardGameDisDTO game(String name, String description, int minPlayers, int maxPlayers,
            String characteristics) {
        BoardGameDisDTO game = new BoardGameDisDTO();
        game.setName(name);
        game.setDescription(description);
        game.setMinPlayers(minPlayers);
        game.setMaxPlayers(maxPlayers);
        game.setTags(List.of(characteristics.split(", ")).stream().map(tagName -> {
            TagDTO tag = new TagDTO();
            tag.setName(tagName);
            return tag;
        }).toList());
        return game;
    }
}
