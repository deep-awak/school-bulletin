package api.poja.app.security;

import api.poja.app.model.Role;
import lombok.*;

import java.util.UUID;

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