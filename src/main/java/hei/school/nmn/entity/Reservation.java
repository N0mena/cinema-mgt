package hei.school.nmn.entity;

import hei.school.nmn.entity.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Reservation(UUID id, User user, Projection projection, ReservationStatus status, List<Seat> seats, LocalDateTime reservedAt) {
}
