package Bakan.Sistema.de.Venta.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desactivar CSRF para APIs
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()  // Permitir todo
                );
        return http.build();
    }
}