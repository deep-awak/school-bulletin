package api.poja.app.model;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CourseTeaching {
  private UUID id;
  private UUID courseId;
  private String teacherId;
  private String groupId;
  private String academicYear;
}