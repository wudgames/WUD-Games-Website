package edu.wisc.union.websiteBackend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EquipmentCheckoutRepository extends JpaRepository<EquipmentCheckout, Long> {
    void deleteByEquipment(Equipment equipment);

    List<EquipmentCheckout> findByEquipmentAndActiveTrue(Equipment equipment);

    @Query("SELECT e.equipment.id, e.equipment.name, COUNT(e) AS totalCheckouts " +
            "FROM EquipmentCheckout e " +
            "WHERE (:startDate IS NULL OR CAST(e.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(e.checkedOutAt AS localdate) <= :endDate) " +
            "GROUP BY e.equipment.id, e.equipment.name " +
            "ORDER BY totalCheckouts DESC")
    List<Object[]> findMostPopularEquipment(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(e) FROM EquipmentCheckout e " +
            "WHERE (:startDate IS NULL OR CAST(e.checkedOutAt AS localdate) >= :startDate) " +
            "AND (:endDate IS NULL OR CAST(e.checkedOutAt AS localdate) <= :endDate)")
    Integer findTotalCheckouts(LocalDate startDate, LocalDate endDate);
}
