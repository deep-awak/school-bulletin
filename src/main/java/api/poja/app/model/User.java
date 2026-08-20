package api.poja.app.model;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class User {
  private UUID id;
  private String email;
  private String fullName;
  private String password;
  private Role role;
  private String studentId;
  private String teacherId;
}
