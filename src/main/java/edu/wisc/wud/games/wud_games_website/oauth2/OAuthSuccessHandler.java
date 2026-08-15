package edu.wisc.wud.games.wud_games_website.oauth2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

    @Autowired
    private GitService gitService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("running onAuthenticationSuccess");
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        System.out.println("rauthentication.getPrincipal() ran");
        String email = oauthUser.getAttribute("email");
        System.out.println("oauthUser.getAttribute(\"email\") ran");
        if (null == email) {
            System.out.println("public email is null");
            /* 
            if (authentication instanceof OAuth2AuthenticationToken token) {
                System.out.println("found token " + token.getAuthorizedClientRegistrationId());
                if (token.getPrincipal() instanceof OidcUser user) {// This step is the issue
                    System.out.println("found user");
                    email = gitService.getPrimaryEmail(user.getIdToken().getTokenValue());
                    System.out.println("primary email is " + email);
                } 
            }
            */


            email = gitService.getPrimaryEmail();
            System.out.println("primary email is " + email);
        }

        if (null == email) {
            throw new ServletException("Email not found in OAuth response");
        }
        System.out.println("#1");
        UserAccount userAccount = userAccountRepository.findByEmail(email);
        if (userAccount == null) {
            System.out.println("userAccount is null");
            throw new UsernameNotFoundException("UserAccount not found");
        }
        System.out.println("#2");
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        System.out.println("#3");
        // Add the user's role from the database to the authorities
        authorities.addAll(userAccount.getAuthorities()); // eg- ADMIN
        System.out.println(userAccount.getAuthorities());
        System.out.println("#4");
        // Create a new Authentication token with the merged authorities
        Authentication newAuth = new OAuth2AuthenticationToken(
                oauthUser, 
                authorities, 
                oauthUser.getName()
        );

        // Setting the updated authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        System.out.println("#5");
        // Redirect the user
        new DefaultRedirectStrategy().sendRedirect(request, response, "/api/user");
    }
}
