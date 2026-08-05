package hei.school.nmn.conf;

import hei.school.nmn.entity.enums.UserRole;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTestFactory {

  private final JwtEncoder jwtEncoder;

  public String bearerFor(UUID userId, UserRole role) {
    return "Bearer " + tokenFor(userId, role);
  }

  public String tokenFor(UUID userId, UserRole role) {
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("cinema-mgt")
            .subject(userId.toString())
            .claim("role", role.name())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .build();
    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}
