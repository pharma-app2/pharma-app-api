package org.pharma.app.pharmaappapi.security.config;

import org.pharma.app.pharmaappapi.security.exceptions.CustomAuthEntryPoint;
import org.pharma.app.pharmaappapi.security.jwt.AuthTokenJwtFilter;
import org.pharma.app.pharmaappapi.security.jwt.ExceptionHandlerFilter;
import org.pharma.app.pharmaappapi.security.services.RoleAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final RoleAuthenticationProvider roleAuthenticationProvider;

    public SecurityConfig(CustomAuthEntryPoint customAuthEntryPoint, RoleAuthenticationProvider roleAuthenticationProvider) {
        this.roleAuthenticationProvider = roleAuthenticationProvider;
        this.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);
        http.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(e -> e.authenticationEntryPoint(customAuthEntryPoint));

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/auth/**").anonymous();

            // Documentation
            auth.requestMatchers(
                    "/favicon.ico",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
            ).permitAll();

            // Public endpoints
            auth.requestMatchers("/api/public/**").permitAll();
            auth.requestMatchers("/error").permitAll();

            auth.requestMatchers(
                    "/api/test/**",
                    "/images/**",
                    "/h2-console/**",
                    "/webjars/**"
            ).permitAll();

            auth.anyRequest().authenticated();
        });

        http.authenticationProvider(roleAuthenticationProvider);

        http.addFilterBefore(authTokenJwtFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(exceptionHandlerFilterBean(), AuthTokenJwtFilter.class);

        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public AuthTokenJwtFilter authTokenJwtFilterBean() {
        return new AuthTokenJwtFilter();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ExceptionHandlerFilter exceptionHandlerFilterBean() {
        return new ExceptionHandlerFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
