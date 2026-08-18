package edu.wisc.wud.games.wud_games_website.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Configuration
//@ConditionalOnProperty(name = "elk.security-enabled", havingValue = "false")
@RequiredArgsConstructor
public class ElasticsearchUnsecuredConfig extends ElasticsearchConfiguration {

	@Getter
	private final ElasticsearchProperties elkProperties;

	@Override
	public ClientConfiguration clientConfiguration() {
        System.out.println("Elasticsearch Unsecured Config is being called");// this never runs
		return ClientConfiguration.builder()
				.connectedTo("localhost:2345")// this is not being respected
				.build();
	}
	
}