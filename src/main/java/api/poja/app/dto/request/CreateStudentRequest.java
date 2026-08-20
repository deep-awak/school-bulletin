package api.poja.app.dto.request;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateStudentRequest {
  private String std;
  private String firstName;
  private String lastName;
  private String email;
  private UUID promotionId;
  private String groupId;
}
