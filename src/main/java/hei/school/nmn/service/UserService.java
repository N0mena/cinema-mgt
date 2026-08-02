package hei.school.nmn.service;

import hei.school.nmn.entity.User;
import hei.school.nmn.mapper.UserMapper;
import hei.school.nmn.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository repository;

    public User getById(UUID id) {
        return mapper.toModel(
                repository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }
}
