package edu.wisc.wud.games.wud_games_website.rental_request;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/rentalRequests", produces = MediaType.APPLICATION_JSON_VALUE)
public class RentalRequestResource {

    private final RentalRequestService rentalRequestService;

    public RentalRequestResource(final RentalRequestService rentalRequestService) {
        this.rentalRequestService = rentalRequestService;
    }

    @GetMapping
    public ResponseEntity<List<RentalRequestDTO>> getAllRentalRequests() {
        return ResponseEntity.ok(rentalRequestService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalRequestDTO> getRentalRequest(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(rentalRequestService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createRentalRequest(
            @RequestBody @Valid final RentalRequestDTO rentalRequestDTO) {
        final Long createdId = rentalRequestService.create(rentalRequestDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateRentalRequest(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final RentalRequestDTO rentalRequestDTO) {
        rentalRequestService.update(id, rentalRequestDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalRequest(@PathVariable(name = "id") final Long id) {
        rentalRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

