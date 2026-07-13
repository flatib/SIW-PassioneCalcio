package it.uniroma3.siw.authentication;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	private final DataSource dataSource;

	public SecurityConfiguration(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
		manager.setUsersByUsernameQuery(
				"SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
		manager.setAuthoritiesByUsernameQuery(
				"SELECT username, role FROM credentials WHERE username=?");
		return manager;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/rest/**"));
		
		http.authorizeHttpRequests(authorize -> authorize
			    .requestMatchers(
			        "/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/error"
			    ).permitAll()

			    .requestMatchers(HttpMethod.GET,
			        "/tournaments", "/tournaments/*",
			        "/teams", "/teams/*",
			        "/matches", "/matches/*",
			        "/players", "/players/*"
			    ).permitAll()
			    .requestMatchers(HttpMethod.POST, "/matches/*/comments").hasAnyAuthority("USER", "ADMIN")
			    .requestMatchers(HttpMethod.GET, "/comments/*/edit").hasAnyAuthority("USER", "ADMIN")
			    .requestMatchers(HttpMethod.POST, "/comments/*/edit").hasAnyAuthority("USER", "ADMIN")
			    .requestMatchers(HttpMethod.GET, "/rest/**").permitAll()
			    .requestMatchers(HttpMethod.POST, "/rest/admin/**").hasAuthority("ADMIN")
			    .requestMatchers(HttpMethod.PUT, "/rest/admin/**").hasAuthority("ADMIN")
			    .requestMatchers(HttpMethod.DELETE, "/rest/admin/**").hasAuthority("ADMIN")
			    .requestMatchers(HttpMethod.GET, "/rest/admin/**").hasAuthority("ADMIN")
			    .requestMatchers("/admin/**").hasAuthority("ADMIN")
			    .anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login?error=true")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/?logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.clearAuthentication(true)
				.permitAll()
			);
		
		http.cors(Customizer.withDefaults());

		return http.build();
	}
}