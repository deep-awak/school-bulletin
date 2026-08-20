package api.poja.app.dto.request;

import lombok.*;

import java.time.LocalDate;

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