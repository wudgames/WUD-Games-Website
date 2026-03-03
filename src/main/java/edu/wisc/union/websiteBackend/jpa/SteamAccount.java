package edu.wisc.union.websiteBackend.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "steamAccounts")
@Getter
@Setter
public class SteamAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steam_account_gen")
    @SequenceGenerator(name = "steam_account_gen", sequenceName = "steam_account_seq")
    @Column(name = "STEAM_ACCOUNT_ID", nullable = false)
    private Long id;

    private String steamAccountUsername;

    private boolean available = true;

    private String notes;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<SteamGame> gamesOnAccount;

    @Column(updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;
}
