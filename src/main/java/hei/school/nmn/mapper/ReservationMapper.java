package hei.school.nmn.mapper;

import hei.school.nmn.endpoint.dto.request.ReservationRequest;
import hei.school.nmn.endpoint.dto.response.ReservationResponse;
import hei.school.nmn.entity.Projection;
import hei.school.nmn.entity.Reservation;
import hei.school.nmn.entity.Seat;
import hei.school.nmn.entity.User;

import java.util.List;

public class ReservationMapper {
    public static Reservation toEntity(ReservationRequest request, User user, Projection projection, List<Seat> seats) {
        return Reservation.builder()
                .user(user)
                .projection(projection)
                .seats(seats)
                .build();
    }

    public static ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.id(),
                UserMapper.toResponse(reservation.user()),
                reservation.projection(),
                reservation.seats(),
                reservation.reservedAt()
        );
    }
}
