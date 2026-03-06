package edu.wisc.wud.games.wud_games_website.account_dis;

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
@RequestMapping(value = "/api/accountDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountDisResource {

    private final AccountDisService accountDisService;

    public AccountDisResource(final AccountDisService accountDisService) {
        this.accountDisService = accountDisService;
    }

    @GetMapping
    public ResponseEntity<List<AccountDisDTO>> getAllAccountDiss() {
        return ResponseEntity.ok(accountDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDisDTO> getAccountDis(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(accountDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createAccountDis(
            @RequestBody @Valid final AccountDisDTO accountDisDTO) {
        final Long createdId = accountDisService.create(accountDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAccountDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AccountDisDTO accountDisDTO) {
        accountDisService.update(id, accountDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountDis(@PathVariable(name = "id") final Long id) {
        accountDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

