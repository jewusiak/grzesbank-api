package pl.jewusiak.grzesbankapi;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import pl.jewusiak.grzesbankapi.service.UserService;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfiguration {
    @Value("${pl.jewusiak.grzesbankapi.cors.appbasehost}")
    private String appBaseHost;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) throws Exception {
        http.sessionManagement(config ->
                config.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredSessionStrategy(event -> event.getResponse().sendError(401, "Expired session")));
        http.authorizeHttpRequests(config -> config.requestMatchers("/auth/**", "/error").permitAll().anyRequest().authenticated());
        http.exceptionHandling(config-> {
            config.authenticationEntryPoint(authenticationEntryPoint());
        });
        http.logout(config -> config.logoutUrl("/auth/logout").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)));
        http.cors(config->{
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList(appBaseHost));
            configuration.setAllowedMethods(Arrays.asList("*"));
            configuration.setAllowedHeaders(Arrays.asList("*"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            config.configurationSource(source);
        });
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.debug("Pre-authenticated entry point called. Rejecting access");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter requestLoggingFilter = new CommonsRequestLoggingFilter();
        requestLoggingFilter.setIncludeClientInfo(true);
        requestLoggingFilter.setIncludeHeaders(true);
        requestLoggingFilter.setIncludeQueryString(true);
        requestLoggingFilter.setIncludePayload(true);
        return requestLoggingFilter;
    }

}
