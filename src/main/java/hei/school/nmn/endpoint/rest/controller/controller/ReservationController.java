package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.endpoint.dto.request.ReservationRequest;
import hei.school.nmn.endpoint.dto.response.ReservationResponse;
import hei.school.nmn.entity.enums.UserRole;
import hei.school.nmn.service.ReservationService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping("/reservations")
  public List<ReservationResponse> getAll() {
    return reservationService.getAll();
  }

  @GetMapping("/reservations/{id}")
  public ReservationResponse getById(
      @AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    ReservationResponse reservation = reservationService.getById(id);
    UUID principalId = UUID.fromString(jwt.getSubject());
    String role = jwt.getClaimAsString("role");
    boolean canSeeAny =
        UserRole.MANAGER.name().equals(role) || UserRole.EMPLOYEE.name().equals(role);
    if (!canSeeAny && !reservation.user().id().equals(principalId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Only the owner can access this reservation");
    }
    return reservation;
  }

  @PostMapping("/reservations")
  public ReservationResponse create(
      @AuthenticationPrincipal Jwt jwt, @RequestBody ReservationRequest request) {
    return reservationService.create(request, UUID.fromString(jwt.getSubject()));
  }
}
