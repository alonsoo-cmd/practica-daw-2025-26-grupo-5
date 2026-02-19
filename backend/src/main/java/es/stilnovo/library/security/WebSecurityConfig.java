package es.stilnovo.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        // Keep CSRF enabled (default)

        http
            .authorizeHttpRequests(authorize -> authorize
                // PUBLIC PAGES
                .requestMatchers("/", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/banned").permitAll()

                // AUTH
                .requestMatchers("/login-page", "/login-error").permitAll()
                .requestMatchers("/signup-page").permitAll()

                .requestMatchers("/product-images/**").permitAll()
                .requestMatchers("/info-product-page/**").permitAll()
                .requestMatchers("/about-page/**").permitAll()
                .requestMatchers("/user/*/profile-photo").permitAll()

                // PRIVATE
                .requestMatchers("/payment-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/contact-seller-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/add-product-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/edit-product-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/sales-and-orders-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/statistics-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/user-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/user-products-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/user-setting-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/favorite-products-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/help-center-page/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/pdf/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/notifications/**").hasAnyRole("USER", "ADMIN")

                // ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/ban/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login-page")
                .failureUrl("/login-error")              // moved to failureHandler to keep banned logic
                .failureHandler(failureHandler)          // mantiene lógica de baneados
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}

