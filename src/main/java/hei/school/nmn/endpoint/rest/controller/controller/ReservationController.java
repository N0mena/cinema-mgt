package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.endpoint.dto.response.ReservationResponse;
import hei.school.nmn.service.ReservationService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping("/reservations")
  public List<ReservationResponse> getAll() {
    return reservationService.getAll();
  }

  @GetMapping("/reservations/{id}")
  public ReservationResponse getById(@PathVariable UUID id) {
    return reservationService.getById(id);
  }
}
