package api.poja.app.security;

import api.poja.app.exception.UnauthorizedException;
import api.poja.app.jpa.UserEntity;
import api.poja.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthContext {

  private final UserRepository userRepository;

  public CurrentUser getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException("User not authenticated");
    }

    String email = authentication.getName();
    UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("User not found: " + email));

    return CurrentUser.builder()
            .userId(user.getId())
            .role(user.getRole().getName())
            .studentId(user.getStudentId())
            .teacherId(user.getTeacherId())
            .build();
  }
}