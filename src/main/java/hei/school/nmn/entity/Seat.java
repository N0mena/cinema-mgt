package hei.school.nmn.entity;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Seat(UUID id, String number, Room room) {
}
