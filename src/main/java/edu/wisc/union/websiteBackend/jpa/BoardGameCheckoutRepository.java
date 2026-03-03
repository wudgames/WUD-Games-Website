package edu.wisc.union.websiteBackend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BoardGameCheckoutRepository extends JpaRepository<BoardGameCheckout, Long> {
    void deleteByBoardGame(BoardGame boardGame);

    List<BoardGameCheckout> findByCheckedOutAtBetween(LocalDateTime start, LocalDateTime end);

    List<BoardGameCheckout> findByBoardGameAndActiveTrue(BoardGame boardGame);

    @Query("SELECT b.boardGame.id, b.boardGame.name, COUNT(b) AS totalCheckouts " +
            "FROM BoardGameCheckout b " +
            "WHERE (:startDate IS NULL OR CAST(b.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(b.checkedOutAt AS localdate) <= :endDate) " +
            "GROUP BY b.boardGame.id, b.boardGame.name " +
            "ORDER BY totalCheckouts DESC")
    List<Object[]> findMostPopularGame(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(sub.cnt) FROM (SELECT COUNT(b) AS cnt FROM BoardGameCheckout b " +
            "WHERE (:startDate IS NULL OR CAST(b.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(b.checkedOutAt AS localdate) <= :endDate) " +
            "GROUP BY CAST(b.checkedOutAt AS localdate)) sub")
    Double findAverageGamesCheckout(LocalDate startDate, LocalDate endDate);

    @Query("SELECT CAST(b.checkedOutAt AS localdate), COUNT(b) AS totalCheckouts " +
            "FROM BoardGameCheckout b " +
            "WHERE (:startDate IS NULL OR CAST(b.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(b.checkedOutAt AS localdate) <= :endDate) " +
            "GROUP BY CAST(b.checkedOutAt AS localdate) " +
            "ORDER BY totalCheckouts DESC")
    List<Object[]> findMostPopularGameNight(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(b) FROM BoardGameCheckout b " +
            "WHERE (:startDate IS NULL OR CAST(b.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(b.checkedOutAt AS localdate) <= :endDate)")
    Integer findTotalCheckouts(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG((b.boardGame.minPlayerCount + b.boardGame.maxPlayerCount) / 2.0) " +
            "FROM BoardGameCheckout b " +
            "WHERE (:startDate IS NULL OR CAST(b.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(b.checkedOutAt AS localdate) <= :endDate)")
    Double findAveragePlayersPerGame(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG((b.boardGame.minPlaytime + b.boardGame.maxPlaytime) / 2.0) " +
            "FROM BoardGameCheckout b " +
            "WHERE (:startDate IS NULL OR CAST(b.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(b.checkedOutAt AS localdate) <= :endDate)")
    Double findAveragePlaytimePerGame(LocalDate startDate, LocalDate endDate);
}
