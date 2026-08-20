package api.poja.app.security;

import api.poja.app.model.Role;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CurrentUser {
  private UUID userId;
  private Role role;
  private String studentId;
  private String teacherId;

  public boolean isAdmin() {
    return role == Role.ADMIN;
  }

  public boolean isTeacher() {
    return role == Role.TEACHER;
  }

  public boolean isStudent() {
    return role == Role.STUDENT;
  }
}
