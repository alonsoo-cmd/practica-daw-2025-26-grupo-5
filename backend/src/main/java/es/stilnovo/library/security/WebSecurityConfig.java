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

/** Spring Security configuration for the application.
 *  Defines access rules, authentication, password encoding, and CSRF protection. */
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

                // PUBLIC
                .requestMatchers("/", "/error").permitAll()
                .requestMatchers("/css/**", "/javascript/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/banned").permitAll()
                .requestMatchers("/login-page", "/login-error", "/signup-page").permitAll()
                .requestMatchers("/product-images/**").permitAll()
                .requestMatchers("/info-product-page/**").permitAll()
                .requestMatchers("/about-page/**").permitAll()

                // profile photos (nuevo sistema)
                .requestMatchers("/user/me/profile-photo").permitAll()

                // You can scroll without login
                .requestMatchers("/load-more-products").permitAll()

                // USER / ADMIN
                .requestMatchers(
                    "/payment-page/**",
                    "/contact-seller-page/**",
                    "/add-product-page/**",
                    "/edit-product-page/**",
                    "/sales-and-orders-page/**",
                    "/statistics-page/**",
                    "/user-page",
                    "/user-products-page",
                    "/user-setting-page",
                    "/favorite-products-page/**",
                    "/help-center-page/**",
                    "/pdf/**",
                    "/api/v1/notifications/**"
                ).hasAnyRole("USER", "ADMIN")

                // ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")

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

