package edu.wisc.wud.games.wud_games_website.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

// This is the configuration for the REST client used in the application.
// It is user to query the git hub api for getting primary email of a user.
@Configuration
public class RestClientConfig {
    @Autowired
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @Bean
    public RestClient restClient() {
        System.out.println("Created RestClient with OAuth2ClientHttpRequestInterceptor");
        //oAuth2AuthorizedClientManager.setClientRegistrationIdResolver(clientRegistrationIdResolver());
        return RestClient.builder()
                // ...
                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(oAuth2AuthorizedClientManager))
                .build();
    }
}
