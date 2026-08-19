package api.poja.app.dto;

import java.time.Instant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GradeHistoryDto {
  private Long id;
  private Long gradeId;
  private Double oldValue;
  private Double newValue;
  private String reason;
  private Long authorUserId;
  private Instant changedAt;
}
