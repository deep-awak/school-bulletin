package api.poja.app.dto;

import java.time.Instant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GradeDto {
  private Long id;
  private Long studentId;
  private Long courseId;
  private String academicYear;
  private Double value;
  private Long authorUserId;
  private Instant createdAt;
  private Instant updatedAt;
}
