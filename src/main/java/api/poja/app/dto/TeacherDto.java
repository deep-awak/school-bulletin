package api.poja.app.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class TeacherDto {
  private String id;
  private String firstName;
  private String lastName;
  private String email;
}
