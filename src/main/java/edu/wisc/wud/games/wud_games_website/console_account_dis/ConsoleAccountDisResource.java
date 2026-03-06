package edu.wisc.wud.games.wud_games_website.console_account_dis;

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
@RequestMapping(value = "/api/consoleAccountDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConsoleAccountDisResource {

    private final ConsoleAccountDisService consoleAccountDisService;

    public ConsoleAccountDisResource(final ConsoleAccountDisService consoleAccountDisService) {
        this.consoleAccountDisService = consoleAccountDisService;
    }

    @GetMapping
    public ResponseEntity<List<ConsoleAccountDisDTO>> getAllConsoleAccountDiss() {
        return ResponseEntity.ok(consoleAccountDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsoleAccountDisDTO> getConsoleAccountDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(consoleAccountDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createConsoleAccountDis(
            @RequestBody @Valid final ConsoleAccountDisDTO consoleAccountDisDTO) {
        final Long createdId = consoleAccountDisService.create(consoleAccountDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateConsoleAccountDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ConsoleAccountDisDTO consoleAccountDisDTO) {
        consoleAccountDisService.update(id, consoleAccountDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsoleAccountDis(@PathVariable(name = "id") final Long id) {
        consoleAccountDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

