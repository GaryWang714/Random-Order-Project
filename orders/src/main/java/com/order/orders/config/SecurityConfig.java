package com.order.orders.config;

import com.order.orders.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // config defines which endpoints are public vs protected
    // config defines how passwords are checked
    // config defines where the jwt filter sits in request chain
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // csrf is build in attack protection for spring
        // disabled since using jwt tokens

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // request matchers below allowing /auth/** which is login and register endpoints to be public
                        // ^ /swagger-ui/** and /v3/api-docs/** makes swagger public for devs to read docs
                        // then anything else requires jwt token
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                // tells spring to not create sessions. spring normally remembers who you are with session cookie
                // with jwt we don't need this since token carries identity on every request
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // put jwtauthfilter in security chain so jwt check happens first
                .authenticationProvider(authenticationProvider())
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // loads user from db (check if user exist)
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        // compares passwords using bcrypt (check if password matches)
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // coordinator handling actual login process
        // when user submits their details, authenticationManager...
        // takes credentials, passes to authenticationProvider, call userDetailsService to load user from db,....
        // run passwordEncoding to compare passwords, then return success or failure
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // bcrypt is a hashing alg. when user logs in, bcrypt hashes what they typed and compares the two hashes
        return new BCryptPasswordEncoder();
    }

}
