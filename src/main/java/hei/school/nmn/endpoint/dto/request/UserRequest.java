package hei.school.nmn.endpoint.dto.request;

import hei.school.nmn.entity.enums.UserRole;
import java.time.LocalDate;

public record UserRequest(
    String firstName,
    String lastName,
    LocalDate birthdate,
    String email,
    String password,
    String phone,
    UserRole role) {}
