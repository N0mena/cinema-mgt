package hei.school.nmn.service;

import hei.school.nmn.endpoint.dto.request.LoginRequest;
import hei.school.nmn.endpoint.dto.response.LoginResponse;
import hei.school.nmn.entity.User;
import hei.school.nmn.service.exception.NotFoundException;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class AuthService {

  private static final long TOKEN_TTL_SECONDS = 3600;

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtEncoder jwtEncoder;

  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest request) {
    User user;
    try {
      user = userService.getEntityByEmail(request.email());
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    if (!passwordEncoder.matches(request.password(), user.password())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("cinema-mgt")
            .subject(user.id().toString())
            .claim("role", user.role().name())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(TOKEN_TTL_SECONDS))
            .build();
    String token =
        jwtEncoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();
    return new LoginResponse(token);
  }
}
