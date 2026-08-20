package api.poja.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CourseTeaching {
  private Long id;
  private Long courseId;
  private Long teacherId;
  private Long groupId;
  private String academicYear;
}
