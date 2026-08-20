package api.poja.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Course {
  private Long id;
  private String name;
  private String code;
}
