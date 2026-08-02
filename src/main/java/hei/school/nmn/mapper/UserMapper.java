package hei.school.nmn.mapper;

import hei.school.nmn.endpoint.dto.request.UserRequest;
import hei.school.nmn.endpoint.dto.response.UserResponse;
import hei.school.nmn.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.birthdate(),
                user.email(),
                user.phone(),
                user.role()
        );
    }

}
