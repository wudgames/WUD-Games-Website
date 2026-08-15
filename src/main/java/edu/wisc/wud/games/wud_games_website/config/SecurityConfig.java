package edu.wisc.wud.games.wud_games_website.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.config.Customizer.withDefaults;

import edu.wisc.wud.games.wud_games_website.oauth2.CustomUserDetailsService;
import edu.wisc.wud.games.wud_games_website.oauth2.OAuthSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CustomUserDetailsService customUserDetailService;

	@Autowired
	private OAuthSuccessHandler handler;
	/*
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
 	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		System.out.println("regestering customUserDetailService");
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customUserDetailService);
		daoAuthenticationProvider.setPasswordEncoder(encoder());
		return daoAuthenticationProvider;
	}
	*/

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(c->c.disable());
		
		httpSecurity.authorizeHttpRequests(authorize->{
			authorize.requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
			.requestMatchers("/error", "/webjars/**").permitAll()
			.requestMatchers("/login/**").permitAll()
			.requestMatchers("/api/user/**").authenticated()
			.anyRequest().permitAll(); 
		});

//		httpSecurity.csrf(csrf->csrf.ignoringRequestMatchers("/logout"));
		
		httpSecurity.formLogin(login->{
			login.loginPage("/login")
			.loginProcessingUrl("/authenticate")
			.usernameParameter("email")
			.passwordParameter("password");
		});
		

		httpSecurity.logout(logout->{
			logout.logoutUrl("/logout")
			.logoutSuccessUrl("/?logout=true");
		});

		/*
		RestOperations restTemplate = new RestTemplate();

		DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService().setRestOperations(restTemplate);
		
		httpSecurity.oauth2Login((oauth2) -> oauth2
			    .userInfoEndpoint(())
			    )
			);
		*/
		
		httpSecurity.oauth2Login(oauth -> {
				oauth.successHandler(handler);
		    });
		
		//httpSecurity.oauth2Login(withDefaults());


		
		return httpSecurity.build();
		
	}
	/*
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return this.githubClientRegistrationRepository();
	}

	@Bean(name = "github")
    public ClientRegistrationRepository githubClientRegistrationRepository() {

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("github")
                .clientId("GITHUB_CLIENT_ID")
                .clientSecret("GITHUB_CLIENT_SECRET")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("/login/oauth2/code/github")
                .scope("read:user")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .userNameAttributeName("id")
                .clientName("gitHub")
                .build();

        return new InMemoryClientRegistrationRepository(clientRegistration);

    }
		 */
}