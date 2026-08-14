package edu.wisc.wud.games.wud_games_website.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.wisc.wud.games.wud_games_website.oauth2.user_account.UserAccountRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserAccountRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("running CustomUserDetailsService.loadUserByUsername() with " + email);
		System.out.println(repository.findByEmail(email));

		return repository.findByEmail(email);
	}

}
