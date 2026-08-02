package hei.school.nmn.mapper;

import hei.school.nmn.entity.User;
import hei.school.nmn.entity.model.JUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public User toModel(JUser entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthdate(entity.getBirthdate())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .role(entity.getRole())
                .build();
    }

    public List<User> toModel(List<JUser> entities) {
        return entities.stream().map(this::toModel).toList();
    }

    public JUser toEntity(User model) {
        return JUser.builder()
                .id(model.id())
                .firstName(model.firstName())
                .lastName(model.lastName())
                .birthdate(model.birthdate())
                .email(model.email())
                .phone(model.phone())
                .role(model.role())
                .build();
    }

}
