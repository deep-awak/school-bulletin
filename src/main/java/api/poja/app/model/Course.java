package api.poja.app.model;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Course {
  private UUID id;
  private String name;
  private String code;
}
