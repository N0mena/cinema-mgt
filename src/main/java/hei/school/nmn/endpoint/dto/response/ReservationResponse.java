package hei.school.nmn.endpoint.dto.response;

import hei.school.nmn.entity.Projection;
import hei.school.nmn.entity.Seat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservationResponse(
    UUID id,
    UserResponse user,
    Projection projection,
    List<Seat> seats,
    LocalDateTime reservedAt) {}
