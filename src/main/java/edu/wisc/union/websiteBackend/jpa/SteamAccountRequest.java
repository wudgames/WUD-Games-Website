package edu.wisc.union.websiteBackend.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "steamAccountRequests")
@Getter
@Setter
public class SteamAccountRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steam_request_gen")
    @SequenceGenerator(name = "steam_request_gen", sequenceName = "steam_request_seq")
    @Column(name = "request_id", nullable = false)
    private Long id;


    private String status; // PENDING, APPROVED, DENIED, RETURNED

    private String name;
    private String email;
    private String gameName;
    private String comments;
    private LocalDate rentalStartDay;
    private LocalDate rentalEndDay;

    @ManyToOne
    private SteamAccount assignedAccount;

    @Column(updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;
}
