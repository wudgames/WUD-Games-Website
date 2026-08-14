package edu.wisc.wud.games.wud_games_website.oauth2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.oauth2.user_account.UserAccount;
import edu.wisc.wud.games.wud_games_website.oauth2.user_account.UserAccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("running onAuthenticationSuccess");
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        UserAccount userAccount = userAccountRepository.findByEmail(email);
        if (userAccount == null) {
            throw new UsernameNotFoundException("UserAccount not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());

        // Add the user's role from the database to the authorities
        authorities.addAll(userAccount.getAuthorities()); // eg- ADMIN
        System.out.println(userAccount.getAuthorities());
        
        // Create a new Authentication token with the merged authorities
        Authentication newAuth = new OAuth2AuthenticationToken(
                oauthUser, 
                authorities, 
                oauthUser.getName()
        );

        // Setting the updated authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        // Redirect the user
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user");
    }
}
