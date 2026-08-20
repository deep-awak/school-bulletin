package api.poja.app.dto.request;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateGradeRequest {
  private String studentId;
  private UUID courseId;
  private String academicYear;
  private Double value;
}
