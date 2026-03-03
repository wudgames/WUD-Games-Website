package edu.wisc.union.websiteBackend.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_checkouts")
@Getter
@Setter
@NoArgsConstructor
public class EquipmentCheckout {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equip_checkout_gen")
    @SequenceGenerator(name = "equip_checkout_gen", sequenceName = "equip_checkout_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false)
    private LocalDateTime checkedOutAt;

    private LocalDateTime returnedAt;

    @Column(nullable = false)
    private String checkedOutBy;

    @Column(nullable = false)
    private boolean active = true;
}
