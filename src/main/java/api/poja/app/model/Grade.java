package api.poja.app.model;

import java.time.Instant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Grade {
  private Long id;
  private Long studentId;
  private Long courseId;
  private String academicYear;
  private Double value;
  private Long authorUserId;
  private Instant createdAt;
  private Instant updatedAt;
}
