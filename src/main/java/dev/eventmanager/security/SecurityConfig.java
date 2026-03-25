package dev.eventmanager.security;

import dev.eventmanager.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = false)
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthenticationProvider userAuthenticationProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(CorsConfigurer::disable)
            .sessionManagement(customizer ->
                customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests ->
                requests
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users/auth").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/{userId}").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET,"/locations").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST,"/locations").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/locations/{locationId}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/locations/{locationId}").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT,"/locations/{locationId}").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.POST,"/events").hasAuthority("USER")
                        .requestMatchers(HttpMethod.DELETE,"/events/{eventId}").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.GET,"/events/my").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET,"/events/{eventId}").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT,"/events/{eventId}").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST,"/events/search").hasAnyAuthority("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST,"/events/registrations/{eventId}").hasAuthority("USER")
                        .requestMatchers(HttpMethod.DELETE,"/events/registrations/cancel/{eventId}").hasAuthority("USER")
                        .requestMatchers(HttpMethod.GET,"/events/registrations/my").hasAuthority("USER")

                        .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling
                            .authenticationEntryPoint(customAuthenticationEntryPoint)
                            .accessDeniedHandler(customAccessDeniedHandler))
            .addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(true).ignoring()
                .requestMatchers("/css/**",
                        "/js/**",
                        "/img/**",
                        "/lib/**",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/swagger-config",
                        "/openapi.yaml"
                );
    }

}
