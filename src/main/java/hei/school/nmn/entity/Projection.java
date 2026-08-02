package hei.school.nmn.entity;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record Projection(UUID id, Instant datetime, BigDecimal seatPrice, Movie movie, Room room) {
}
