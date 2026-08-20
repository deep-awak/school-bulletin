package api.poja.app.dto.request;

import java.time.LocalDate;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class AssignStudentGroupRequest {
  private String studentId;
  private String groupId;
  private LocalDate startDate;
}
