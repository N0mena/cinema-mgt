package hei.school.nmn.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConf {

  private final SecretKey secretKey;

  public SecurityConf(
      @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}") String secret) {
    this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, ex) -> {
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          response
                              .getWriter()
                              .write("{\"status\":401,\"message\":\"Unauthorized\"}");
                        })
                    .accessDeniedHandler(
                        (request, response, ex) -> {
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          response.getWriter().write("{\"status\":403,\"message\":\"Forbidden\"}");
                        }))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/ping", "/health/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/rooms",
                        "/api/rooms/{id}",
                        "/api/seats/{id}",
                        "/api/movies",
                        "/api/movies/{id}",
                        "/api/projections",
                        "/api/projections/{id}")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/users", "/api/auth/login")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.PUT, "/api/movies", "/api/movies/{id}", "/api/projection", "/api/projections/{id}")
                    .hasRole("MANAGER")
                        . requestMatchers(
                        HttpMethod.POST,  "/api/projection")
                        .hasRole("MANAGER")
                    .requestMatchers(HttpMethod.GET, "/api/reservations")
                    .hasAnyRole("EMPLOYEE", "MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/api/reservations/{id}")
                    .hasAnyRole("EMPLOYEE", "MANAGER")
                    .requestMatchers(HttpMethod.POST, "/api/reservations")
                    .authenticated()
                    .anyRequest()
                    .authenticated());
    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          String role = jwt.getClaimAsString("role");
          if (role == null) {
            return List.of();
          }
          return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });
    return converter;
  }
}
