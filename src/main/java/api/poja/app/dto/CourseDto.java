package api.poja.app.dto;

import lombok.*;

import java.util.UUID;

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