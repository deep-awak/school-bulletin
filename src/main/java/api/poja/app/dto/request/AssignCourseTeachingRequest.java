package api.poja.app.dto.request;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class AssignCourseTeachingRequest {
  private UUID courseId;
  private String teacherId;
  private String groupId;
  private String academicYear;
}
