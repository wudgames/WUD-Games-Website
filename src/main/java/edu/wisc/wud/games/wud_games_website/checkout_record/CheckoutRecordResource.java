package edu.wisc.wud.games.wud_games_website.checkout_record;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EnableMethodSecurity
@RequestMapping(value = "/api/checkoutRecords", produces = MediaType.APPLICATION_JSON_VALUE)
public class CheckoutRecordResource {

    private final CheckoutRecordService checkoutRecordService;

    public CheckoutRecordResource(final CheckoutRecordService checkoutRecordService) {
        this.checkoutRecordService = checkoutRecordService;
    }

    @PreAuthorize("denyAll")
    @GetMapping
    public ResponseEntity<List<CheckoutRecordDTO>> getAllCheckoutRecords() {
        return ResponseEntity.ok(checkoutRecordService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckoutRecordDTO> getCheckoutRecord(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(checkoutRecordService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createCheckoutRecord(
            @RequestBody @Valid final CheckoutRecordDTO checkoutRecordDTO) {
        final Long createdId = checkoutRecordService.create(checkoutRecordDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCheckoutRecord(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CheckoutRecordDTO checkoutRecordDTO) {
        checkoutRecordService.update(id, checkoutRecordDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheckoutRecord(@PathVariable(name = "id") final Long id) {
        checkoutRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

