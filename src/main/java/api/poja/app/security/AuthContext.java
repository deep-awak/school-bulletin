package api.poja.app.security;

import api.poja.app.exception.UnauthorizedException;
import api.poja.app.jpa.UserEntity;
import api.poja.app.model.Role;
import api.poja.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves the caller's identity for the current request.
 *
 * <p>The project has no login/password flow (out of scope for this exercise); the caller
 * identifies itself with the {@code X-User-Id} header, which must match an existing {@link
 * UserEntity}. This keeps role enforcement (STUDENT/TEACHER/ADMIN) real and testable without
 * building a full authentication stack.
 */
@Component
@AllArgsConstructor
public class AuthContext {
  public static final String USER_ID_HEADER = "X-User-Id";

  private final UserRepository userRepository;

  public CurrentUser resolve(HttpServletRequest request) {
    String header = request.getHeader(USER_ID_HEADER);
    if (header == null || header.isBlank()) {
      throw new UnauthorizedException("Missing " + USER_ID_HEADER + " header");
    }
    Long userId;
    try {
      userId = Long.valueOf(header);
    } catch (NumberFormatException e) {
      throw new UnauthorizedException("Invalid " + USER_ID_HEADER + " header");
    }
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UnauthorizedException("Unknown user: " + userId));

    return CurrentUser.builder()
        .userId(user.getId())
        .role(user.getRole().getName())
        .studentId(user.getStudentId())
        .teacherId(user.getTeacherId())
        .build();
  }
}
