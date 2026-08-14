package edu.wisc.wud.games.wud_games_website.oauth2;
/* 
import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
public class Interceptor implements ClientHttpRequestInterceptor {
    // here you have to replace registrationId with the one that you
    // used in the application.properties
    private static final String REGISTRATION_ID = "github";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public Interceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            // log an error, it can be helpful during debugging
            return execution.execute(request, body);
        }
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest
                .withClientRegistrationId(REGISTRATION_ID).principal(authentication.getName()).build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(req);
        if (authorizedClient == null) {
            // log an error, it can be helpful during debugging
            return execution.execute(request, body);
        }
        // here we're actually using the OAuth2 client that we configured before
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        // this is the most important line, where we set the bearer token value
        request.getHeaders().setBearerAuth(accessToken.getTokenValue());
        return execution.execute(request, body);
    }
}
*/