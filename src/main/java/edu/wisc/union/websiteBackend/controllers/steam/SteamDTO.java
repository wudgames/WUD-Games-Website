package edu.wisc.union.websiteBackend.controllers.steam;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class SteamDTO {

    @Data
    public static class SteamAccountDTO {
        private Long id;
        private String steamAccountUsername;
        private boolean available;
        private String notes;
        private Set<Long> gameIds;
        private LocalDateTime createdAt;
    }

    @Data
    public static class SteamGameDTO {
        private Long id;
        private String steamAppId;
        private String name;
        private String description;
        private String imageUrl;
        private Integer checkoutCount;
        private String internalNotes;
        private Boolean windows;
        private Boolean macos;
        private Boolean linux;
        private LocalDateTime createdAt;
    }

    @Data
    public static class SteamAccountRequestDTO {
        private Long id;
        private String status;
        private String name;
        private String email;
        private String gameName;
        private String comments;
        private LocalDate rentalStartDay;
        private LocalDate rentalEndDay;
        private Long assignedAccountId;
        private LocalDateTime createdAt;
    }
}
