package edu.wisc.wud.games.wud_games_website.console_account;

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
@RequestMapping(value = "/api/consoleAccounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsoleAccountResource {

    private final ConsoleAccountService consoleAccountService;

    public ConsoleAccountResource(final ConsoleAccountService consoleAccountService) {
        this.consoleAccountService = consoleAccountService;
    }

    @GetMapping
    public ResponseEntity<List<ConsoleAccountDTO>> getAllConsoleAccounts() {
        return ResponseEntity.ok(consoleAccountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsoleAccountDTO> getConsoleAccount(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(consoleAccountService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createConsoleAccount(
            @RequestBody @Valid final ConsoleAccountDTO consoleAccountDTO) {
        final Long createdId = consoleAccountService.create(consoleAccountDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateConsoleAccount(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ConsoleAccountDTO consoleAccountDTO) {
        consoleAccountService.update(id, consoleAccountDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsoleAccount(@PathVariable(name = "id") final Long id) {
        consoleAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

