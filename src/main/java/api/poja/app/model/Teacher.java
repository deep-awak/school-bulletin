package api.poja.app.model;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Teacher {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private UUID userId;
}
