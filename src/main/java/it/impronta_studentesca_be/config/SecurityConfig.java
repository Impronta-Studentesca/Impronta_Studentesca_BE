package it.impronta_studentesca_be.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import it.impronta_studentesca_be.constant.ApiPath;
import it.impronta_studentesca_be.security.PersonaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PersonaUserDetailsService personaUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(personaUserDetailsService); // ✅ user details
        provider.setPasswordEncoder(passwordEncoder());            // ✅ encoder
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())

                // ✅ sessione attiva (default è IF_REQUIRED, ma lo metto esplicito)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))


                // 🔹 Gestione errori di sicurezza (401 / 403) in JSON
                .exceptionHandling(ex -> ex
                        // NON autenticato -> 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                  "status": 401,
                                  "error": "Unauthorized",
                                  "message": "Autenticazione richiesta per accedere a questa risorsa"
                                }
                                """);
                        })
                        // Autenticato ma senza permessi -> 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                  "status": 403,
                                  "error": "Forbidden",
                                  "message": "Non hai i permessi per accedere a questa risorsa"
                                }
                                """);
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // Swagger aperto
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()

                        // LOGIN aperta
                        .requestMatchers("/" + ApiPath.BASE_PATH + "/" + ApiPath.AUTH_PATH + "/**").permitAll()

                        // PUBLIC aperto
                        .requestMatchers("/" + ApiPath.BASE_PATH + "/" + ApiPath.PUBLIC_PATH + "/**").permitAll()

                        // ADMIN solo DIRETTIVO
                        .requestMatchers("/" + ApiPath.BASE_PATH + "/" + ApiPath.ADMIN_PATH + "/**")
                        .hasAuthority("DIRETTIVO")

                        .requestMatchers("/" + ApiPath.BASE_PATH + "/" + ApiPath.STAFF_PATH + "/**")
                        .hasAnyAuthority(
                                "DIRETTIVO",
                                "DIRETTIVO_DIPARTIMENTALE",
                                "STAFF",
                                "RAPPRESENTANTE",
                                "RESPONSABILE_UFFICIO"
                        )


                );


        return http.build();
    }



}
