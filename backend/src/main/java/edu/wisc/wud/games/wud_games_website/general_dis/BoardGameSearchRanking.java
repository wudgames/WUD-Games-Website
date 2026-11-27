package edu.wisc.wud.games.wud_games_website.general_dis;

import edu.wisc.wud.games.wud_games_website.game_dis.GameDisDTO;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BoardGameSearchRanking {
    private static final Pattern PLAYER_COUNT = Pattern.compile("(\\d+)\\s*[- ]?\\s*(?:players?|p)\\b");
    private static final Set<String> IGNORED_TERMS = Set.of(
            "a", "an", "and", "are", "board", "game", "games", "for", "i", "is", "looking", "me",
            "of", "play", "player", "players", "the", "to", "want", "with");

    private BoardGameSearchRanking() {
    }

    static List<GeneralDisDTO> rank(List<GeneralDisDTO> games, String searchTerm) {
        String query = normalize(searchTerm);
        if (query.isEmpty()) {
            return games;
        }

        return games.stream()
                .map(game -> new ScoredGame(game, score(game, query, searchTerm)))
                .filter(scored -> scored.score() > 0)
                .sorted(Comparator.comparingInt(ScoredGame::score).reversed()
                        .thenComparing(scored -> safeName(scored.game()), String.CASE_INSENSITIVE_ORDER))
                .map(ScoredGame::game)
                .toList();
    }

    private static int score(GeneralDisDTO game, String query, String rawQuery) {
        String name = normalize(game.getName());
        int score = 0;
        if (name.equals(query)) {
            score += 50;
        } else if (name.startsWith(query)) {
            score += 30;
        } else if (name.contains(query)) {
            score += 20;
        }

        List<String> nameWords = words(name);
        List<String> descriptionWords = words(normalize(game.getDescription()));
        List<String> tagWords = game.getTags() == null ? List.of()
                : game.getTags().stream().filter(tag -> tag.getName() != null)
                        .flatMap(tag -> words(normalize(tag.getName())).stream()).toList();

        int textScore = 0;
        for (String term : words(query).stream().distinct().toList()) {
            if (IGNORED_TERMS.contains(term) || term.chars().allMatch(Character::isDigit)) {
                continue;
            }
            textScore += Math.max(matchWeight(nameWords, term, 8, 4),
                    Math.max(matchWeight(tagWords, term, 7, 3), matchWeight(descriptionWords, term, 3, 1)));
        }
        score += textScore;

        Matcher playerMatch = PLAYER_COUNT.matcher(rawQuery.toLowerCase(Locale.ROOT));
        if (playerMatch.find() && game instanceof GameDisDTO gameDetails) {
            int requestedPlayers = Integer.parseInt(playerMatch.group(1));
            Integer minPlayers = gameDetails.getMinPlayers();
            Integer maxPlayers = gameDetails.getMaxPlayers();
            if ((minPlayers == null || requestedPlayers >= minPlayers)
                    && (maxPlayers == null || requestedPlayers <= maxPlayers)
                    && (textScore > 0 || words(query).stream()
                            .allMatch(term -> IGNORED_TERMS.contains(term) || term.chars().allMatch(Character::isDigit)))) {
                score += 12;
            } else if (textScore > 0) {
                score -= 4;
            }
        }
        return score;
    }

    private static int matchWeight(List<String> words, String term, int exactWeight, int closeWeight) {
        if (words.contains(term)) {
            return exactWeight;
        }
        if (term.length() >= 5 && words.stream().anyMatch(word -> distanceAtMostOne(word, term))) {
            return closeWeight;
        }
        return 0;
    }

    private static boolean distanceAtMostOne(String first, String second) {
        if (Math.abs(first.length() - second.length()) > 1) {
            return false;
        }
        int firstIndex = 0;
        int secondIndex = 0;
        int differences = 0;
        while (firstIndex < first.length() && secondIndex < second.length()) {
            if (first.charAt(firstIndex) == second.charAt(secondIndex)) {
                firstIndex++;
                secondIndex++;
            } else if (++differences > 1) {
                return false;
            } else if (first.length() > second.length()) {
                firstIndex++;
            } else if (second.length() > first.length()) {
                secondIndex++;
            } else {
                firstIndex++;
                secondIndex++;
            }
        }
        return differences + first.length() - firstIndex + second.length() - secondIndex <= 1;
    }

    private static List<String> words(String normalized) {
        return normalized.isEmpty() ? List.of() : List.of(normalized.split(" "));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }

    private static String safeName(GeneralDisDTO game) {
        return game.getName() == null ? "" : game.getName();
    }

    private record ScoredGame(GeneralDisDTO game, int score) {
    }
}
