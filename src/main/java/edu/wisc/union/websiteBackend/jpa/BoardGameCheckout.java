package edu.wisc.union.websiteBackend.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "board_game_checkouts")
@Getter
@Setter
@NoArgsConstructor
public class BoardGameCheckout {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checkout_gen")
    @SequenceGenerator(name = "checkout_gen", sequenceName = "checkout_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_game_id", nullable = false)
    private BoardGame boardGame;

    @Column(nullable = false)
    private LocalDateTime checkedOutAt;

    private LocalDateTime returnedAt;

    @Column(nullable = false)
    private String checkedOutBy;

    private Integer playerCount;

    @Column(nullable = false)
    private boolean active = true;
}
