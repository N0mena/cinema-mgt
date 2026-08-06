package hei.school.nmn.entity;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record Room(
    UUID id, String number, Integer capacity, List<Seat> seats, List<Projection> projections) {}
