package api.poja.app.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class AssignCourseTeachingRequest {
  private Long courseId;
  private Long teacherId;
  private Long groupId;
  private String academicYear;
}
