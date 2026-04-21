package edu.wisc.wud.games.wud_games_website.rental_request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RentalRequestDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String requesterName;

    @NotNull
    @Size(max = 255)
    private String requesterEmail;

    @NotNull
    private OffsetDateTime submissionTime;

    @NotNull
    private OffsetDateTime startDate;

    @NotNull
    private OffsetDateTime endDate;

    private String requesterComments;

    private String adminComments;

    @NotNull
    private RequestStatus status;

    @RentalRequestCheckoutRecordUnique
    private Long checkoutRecord;

    private List<Long> requestedInventory;

}

