package hei.school.nmn.entity;

import java.util.UUID;

public record Seat(UUID id, String number, Room room) {
}
