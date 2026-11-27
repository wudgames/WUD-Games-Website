package edu.wisc.wud.games.wud_games_website.physical_item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;

@Entity
@Getter
public class Barcode {
    @SequenceGenerator(
            name = "barcode_sequence",
            sequenceName = "barcode_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "barcode_sequence"
    )
    @Id
    private Long id;
    void setId(Long id) {
        System.out.println("Setting a barcode id to: " + id);
        this.id = id;
    }

    public Barcode() {
        super();
    }
}
