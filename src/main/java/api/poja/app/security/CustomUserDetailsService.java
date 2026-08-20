package api.poja.app.security;

import api.poja.app.jpa.UserEntity;
import api.poja.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("Searching for user with email: {}", email);

		UserEntity user = userRepository.findByEmail(email)
				.orElseThrow(() -> {
					log.error("User not found with email: {}", email);
					return new UsernameNotFoundException("User not found: " + email);
				});

		log.info("User found: {}, role: {}", user.getEmail(), user.getRole().getName());

		return User.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name())))
				.build();
	}
}