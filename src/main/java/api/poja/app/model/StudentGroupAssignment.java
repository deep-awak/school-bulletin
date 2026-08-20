package api.poja.app.model;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class StudentGroupAssignment {
  private UUID id;
  private String studentId;
  private String groupId;
  private LocalDate startDate;
  private LocalDate endDate;
}