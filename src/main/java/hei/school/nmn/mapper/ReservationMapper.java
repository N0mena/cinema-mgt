package hei.school.nmn.mapper;

import hei.school.nmn.endpoint.dto.response.ReservationResponse;
import hei.school.nmn.entity.Reservation;
import hei.school.nmn.entity.enums.ReservationStatus;
import hei.school.nmn.repository.model.JProjection;
import hei.school.nmn.repository.model.JReservation;
import hei.school.nmn.repository.model.JSeat;
import hei.school.nmn.repository.model.JUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

  public static JReservation toJ(JUser user, JProjection projection, List<JSeat> seats) {
    return JReservation.builder()
        .id(UUID.randomUUID())
        .user(user)
        .projection(projection)
        .status(ReservationStatus.PENDING)
        .seats(seats)
        .reservedAt(LocalDateTime.now())
        .build();
  }

  public static Reservation toDomain(JReservation reservation) {
    return Reservation.builder()
        .id(reservation.getId())
        .user(UserMapper.toDomain(reservation.getUser()))
        .projection(ProjectionMapper.toDomain(reservation.getProjection()))
        .status(reservation.getStatus())
        .seats(
            reservation.getSeats().stream().map(SeatMapper::toDomain).collect(Collectors.toList()))
        .reservedAt(reservation.getReservedAt())
        .build();
  }

  public static ReservationResponse toResponse(Reservation reservation) {
    return new ReservationResponse(
        reservation.id(),
        UserMapper.toResponse(reservation.user()),
        reservation.projection(),
        reservation.seats(),
        reservation.reservedAt());
  }
}
