package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.endpoint.dto.request.LoginRequest;
import hei.school.nmn.endpoint.dto.response.LoginResponse;
import hei.school.nmn.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/auth/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    return authService.login(request);
  }
}
