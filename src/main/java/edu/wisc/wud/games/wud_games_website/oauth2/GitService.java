package edu.wisc.wud.games.wud_games_website.oauth2;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;

import org.springframework.http.HttpHeaders;
import org.apache.hc.core5.http.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Collections;
import java.util.List;

@Service
public class GitService {
    @Autowired
    private RestClient restClient;

    @Autowired
    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    private static final String GITHUB_API_URL = "https://api.github.com";

    public String getPrimaryEmail() {
        String url = GITHUB_API_URL + "/user/emails";
        System.out.println("Getting github emails");
        /*
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", "Bearer " + token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API call
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        */

        //oAuth2AuthorizedClientManager.

        Json emails = restClient.get()
            .uri(url)
            //.attributes(clientRegistrationId("github"))
            .attribute("clientRegistrationId", "github")
            .retrieve()
            .body(Json.class);// Getting authentication error

        System.out.println("emails:\n" + emails);

        throw new UnsupportedOperationException("getPrimaryEmail, Feature incomplete.");
        //return emails[0].email;
    }
}