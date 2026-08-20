package api.poja.app.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

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