package hei.school.nmn.entity;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record Room(UUID id, String number, Integer capacity, List<Seat> seats, List<Projection> projections) {
}
