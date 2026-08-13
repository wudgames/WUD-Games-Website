package edu.wisc.wud.games.wud_games_website.oauth2;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println("principal is null: " + (null == principal));
        System.out.println("principal attribute: " + principal.getAttributes().keySet());
        
        System.out.println("principal login: " + principal.getAttribute("login"));// Display name
        System.out.println("principal id: " + principal.getAttribute("id"));// Identifyer

        System.out.println("principal authorities: " + principal.getAuthorities());// This might me the authorities with respect to github not the authorities of the user in the database.
        return Collections.singletonMap("login", principal.getAttribute("login"));
    }

    // TODO Add way to edit users
}
