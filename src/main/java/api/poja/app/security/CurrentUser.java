package api.poja.app.security;

import api.poja.app.model.Role;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CurrentUser {
  private Long userId;
  private Role role;
  private Long studentId;
  private Long teacherId;

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
