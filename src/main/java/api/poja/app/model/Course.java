package api.poja.app.model;

import lombok.*;

import java.util.UUID;

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