package api.poja.app.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GradeDto {
  private UUID id;
  private String studentId;
  private UUID courseId;
  private String academicYear;
  private Double value;
  private UUID authorUserId;
  private Instant createdAt;
  private Instant updatedAt;
}