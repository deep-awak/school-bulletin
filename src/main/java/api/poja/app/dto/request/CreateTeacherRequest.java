package api.poja.app.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateTeacherRequest {
  private String firstName;
  private String lastName;
  private String email;
}