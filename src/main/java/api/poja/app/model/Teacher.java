package api.poja.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Teacher {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Long userId;
}
