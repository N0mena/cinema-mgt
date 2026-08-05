package hei.school.nmn.entity;

import java.util.UUID;
import lombok.Builder;

@Builder
public record Seat(UUID id, String number, Room room) {}
