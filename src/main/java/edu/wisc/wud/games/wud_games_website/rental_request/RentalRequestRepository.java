package edu.wisc.wud.games.wud_games_website.rental_request;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {

    RentalRequest findFirstByCheckoutRecordId(Long id);

    List<RentalRequest> findAllByRequestedInventoryId(Long id);

    boolean existsByCheckoutRecordId(Long id);

}

