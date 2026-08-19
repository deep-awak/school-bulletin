package api.poja.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class User {
  private Long id;
  private String email;
  private String fullName;
  private Role role;
  private Long studentId;
  private Long teacherId;
}
