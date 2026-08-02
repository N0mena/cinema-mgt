package hei.school.nmn.entity;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record User(UUID id, String firstName, String lastName, LocalDate birthdate, String email, String password, String phone, UserRole role) {
}
