package hei.school.nmn.service;

import hei.school.nmn.endpoint.dto.request.UserRequest;
import hei.school.nmn.endpoint.dto.response.UserResponse;
import hei.school.nmn.entity.User;
import hei.school.nmn.entity.model.JUser;
import hei.school.nmn.mapper.UserMapper;
import hei.school.nmn.repository.UserRepository;
import hei.school.nmn.service.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserResponse register(UserRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalStateException("An account already exists for email: " + request.email());
    }
    JUser jUser = UserMapper.toJ(UserMapper.toEntity(request));
    jUser.setPassword(passwordEncoder.encode(request.password()));
    return UserMapper.toResponse(UserMapper.toDomain(userRepository.save(jUser)));
  }

  @Transactional(readOnly = true)
  public User getEntityById(UUID id) {
    return UserMapper.toDomain(getJUser(id));
  }

  @Transactional(readOnly = true)
  public UserResponse getById(UUID id) {
    return UserMapper.toResponse(getEntityById(id));
  }

  @Transactional(readOnly = true)
  public User getEntityByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .map(UserMapper::toDomain)
        .orElseThrow(() -> new NotFoundException("User not found: " + email));
  }

  @Transactional(readOnly = true)
  public List<UserResponse> getAll() {
    return userRepository.findAll().stream()
        .map(UserMapper::toDomain)
        .map(UserMapper::toResponse)
        .collect(Collectors.toList());
  }

  private JUser getJUser(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));
  }
}
