package edu.wisc.wud.games.wud_games_website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WUDGamesWebciteApplication {
    // Use npm run devserver to start a local server for developing the frontend
    // after doing so run npm run build to generate the frontend files so that they can be served by the Spring Boot server
    public static void main(final String[] args) {
        SpringApplication.run(WUDGamesWebciteApplication.class, args);
    }

}

