package edu.wisc.wud.games.wud_games_website.oauth2.user_account;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableMethodSecurity
public class UserAccountResource {
    private final UserAccountService userAccountService;

    public UserAccountResource(final UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println("principal is null: " + (null == principal));
        System.out.println("principal attribute: " + principal.getAttributes().keySet());
        
        System.out.println("principal login: " + principal.getAttribute("login"));// Display name
        System.out.println("principal id: " + principal.getAttribute("id"));// Identifyer

        System.out.println("principal authorities: " + principal.getAuthorities());// This might me the authorities with respect to github not the authorities of the user in the database.
        return Collections.singletonMap("login", principal.getAttribute("login"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserAccountDTO>> getAllUserAccounts() {
        return ResponseEntity.ok(userAccountService.findAll());
    }

    // TODO Add way to edit users
}
