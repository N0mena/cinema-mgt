package hei.school.nmn.mapper;

import hei.school.nmn.endpoint.dto.request.UserRequest;
import hei.school.nmn.endpoint.dto.response.UserResponse;
import hei.school.nmn.entity.User;
import hei.school.nmn.entity.model.JUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public static User toEntity(UserRequest request) {
    return User.builder()
        .firstName(request.firstName())
        .lastName(request.lastName())
        .birthdate(request.birthdate())
        .email(request.email())
        .password(request.password())
        .phone(request.phone())
        .role(request.role())
        .build();
  }

  public static JUser toJ(User user) {
    return JUser.builder()
        .id(user.id())
        .firstName(user.firstName())
        .lastName(user.lastName())
        .birthdate(user.birthdate())
        .email(user.email())
        .password(user.password())
        .phone(user.phone())
        .role(user.role())
        .build();
  }

  public static User toDomain(JUser user) {
    return User.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .birthdate(user.getBirthdate())
        .email(user.getEmail())
        .password(user.getPassword())
        .phone(user.getPhone())
        .role(user.getRole())
        .build();
  }

  public static UserResponse toResponse(User user) {
    return new UserResponse(
        user.id(),
        user.firstName(),
        user.lastName(),
        user.birthdate(),
        user.email(),
        user.phone(),
        user.role());
  }
}
