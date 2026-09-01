package edu.wisc.wud.games.wud_games_website.general_dis;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.wisc.wud.games.wud_games_website.config.DataInitializer;


public interface GeneralDisRepository extends JpaRepository<GeneralDis, Long> {

    List<GeneralDis> findAllByTagsId(Long id);

    //@Query("Select description FROM GeneralDis WHERE (min_players BETWEEN :minPlayers AND :maxPlaytime) ORDER BY name ASC")
    //List<GeneralDis> search(String query, int minPlayers, int maxPlaytime);

    @Query(value = "SELECT description FROM GeneralDis description WHERE description.name LIKE %:searchText% ORDER BY name ASC")
    List<? extends GeneralDis> search(@Param("searchText") String searchText);

    @Query(value = "SELECT COUNT(*) FROM CheckoutRecord checkoutRecord " + //
                "INNER JOIN checkoutRecord.inventoryItems item " + //
                "INNER JOIN item.genDis description " + //
                "WHERE description.id=:id AND checkoutRecord.returnedTime IS NULL")
    Integer getNumberCheckedOut(Long id);

    @Query(value = "SELECT COUNT(*) FROM CheckoutRecord checkoutRecord " + //
                "INNER JOIN checkoutRecord.inventoryItems item " + //
                "INNER JOIN item.genDis description " + //
                "WHERE description.id=:description_id")
    Integer getTotalNumberOfCheckouts(Long description_id);

    @Query(value = "SELECT COUNT(*) FROM CheckoutRecord checkoutRecord " + //
                "INNER JOIN checkoutRecord.inventoryItems item " + //
                "INNER JOIN item.genDis description " + //
                "WHERE description.id=:description_id AND checkoutRecord.returnedTime=:legacyDate")
    Integer getTotalNumberOfLegacyCheckouts(Long description_id, OffsetDateTime legacyDate);
}

