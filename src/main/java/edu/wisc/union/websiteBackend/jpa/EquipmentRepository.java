package edu.wisc.union.websiteBackend.jpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    @Query("SELECT e FROM Equipment e WHERE " +
            "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:type IS NULL OR LOWER(e.type) = LOWER(:type))")
    List<Equipment> findFiltered(@Param("name") String name,
                                 @Param("type") String type,
                                 Sort order);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT SUM(e.availableCopies) FROM Equipment e")
    Integer findTotalAvailableCopies();

    @Query("SELECT DISTINCT e.type FROM Equipment e WHERE e.type IS NOT NULL ORDER BY e.type")
    List<String> findDistinctTypes();
}
