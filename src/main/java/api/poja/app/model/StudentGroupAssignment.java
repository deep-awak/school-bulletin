package api.poja.app.model;

import java.time.LocalDate;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class StudentGroupAssignment {
  private Long id;
  private Long studentId;
  private Long groupId;
  private LocalDate startDate;
  private LocalDate endDate;
}
