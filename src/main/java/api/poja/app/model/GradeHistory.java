package api.poja.app.model;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GradeHistory {
  private UUID id;
  private UUID gradeId;
  private Double oldValue;
  private Double newValue;
  private String reason;
  private UUID authorUserId;
  private Instant changedAt;
}
