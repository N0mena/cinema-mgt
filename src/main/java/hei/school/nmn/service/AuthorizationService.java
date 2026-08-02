package hei.school.nmn.service;

import hei.school.nmn.entity.User;
import hei.school.nmn.entity.enums.UserRole;
import hei.school.nmn.service.exception.NotFoundException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class AuthorizationService {

    private final UserService userService;

    public void requireManager(UUID userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authenticated user required");
        }
        User user;
        try {
            user = userService.getEntityById(userId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown user: " + userId);
        }
        if (user.role() != UserRole.MANAGER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only managers can update movies");
        }
    }
}
