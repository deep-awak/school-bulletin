package api.poja.app.dto;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CourseDto {
  private UUID id;
  private String name;
  private String code;
}
