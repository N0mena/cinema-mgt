package hei.school.nmn.endpoint.dto.response;

import hei.school.nmn.entity.enums.UserRole;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String firstName,
    String lastName,
    LocalDate birthdate,
    String email,
    String phone,
    UserRole role) {}
