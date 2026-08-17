package edu.wisc.wud.games.wud_games_website.user_account;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@EnableMethodSecurity
public class UserAccountResource {
    private final UserAccountService userAccountService;

    public UserAccountResource(final UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/myuser")
    public ModelAndView myuser(@AuthenticationPrincipal OAuth2User principal, Model model) {

        // Code to debug info about user
        System.out.println("debugging info about user:");
        System.out.println("    principal is null: " + (null == principal));
        Set<String> keyset = principal.getAttributes().keySet();
        System.out.println("    principal attribute: " + keyset);
        for (String key : keyset) {
            System.out.println("    " + key + ": " + principal.getAttribute(key));
        }
        System.out.println("    principal email: " + principal.getAttribute("email"));
        System.out.println("    api email query: " + principal.getAttribute("email"));
        System.out.println("    principal authorities: " + principal.getAuthorities());// This might me the authorities with respect to github not the authorities of the user in the database.

        // Return the email of the authenticated user to the browser
        model.addAttribute("user_email", principal.getAttribute("email"));
        return new ModelAndView("myuser");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/users")
    public ResponseEntity<List<UserAccountDTO>> getAllUserAccounts() {
        return ResponseEntity.ok(userAccountService.findAll());
    }

    // TODO: Add way to edit users

    // TODO: user should be able to change email/disable oauth2 login

    // TODO: user should be able to deleate account
}
