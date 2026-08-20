package api.poja.app.model;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Grade {
  private UUID id;
  private String studentId;
  private UUID courseId;
  private String academicYear;
  private Double value;
  private UUID authorUserId;
  private Instant createdAt;
  private Instant updatedAt;
}
