package api.poja.app.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateGradeRequest {
  private Long studentId;
  private Long courseId;
  private String academicYear;
  private Double value;
}
