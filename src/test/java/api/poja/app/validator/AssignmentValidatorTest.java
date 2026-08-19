package api.poja.app.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import api.poja.app.dto.request.AssignCourseTeachingRequest;
import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.exception.ValidationException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AssignmentValidatorTest {

  private final AssignmentValidator validator = new AssignmentValidator();

  @Test
  void accepts_a_valid_student_group_assignment() {
    var request =
        AssignStudentGroupRequest.builder()
            .studentId(1L)
            .groupId(2L)
            .startDate(LocalDate.of(2025, 9, 1))
            .build();

    assertDoesNotThrow(() -> validator.validate(request));
  }

  @Test
  void rejects_student_group_assignment_without_start_date() {
    var request = AssignStudentGroupRequest.builder().studentId(1L).groupId(2L).build();

    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Start date");
  }

  @Test
  void accepts_a_valid_course_teaching_assignment() {
    var request =
        AssignCourseTeachingRequest.builder()
            .courseId(1L)
            .teacherId(2L)
            .groupId(3L)
            .academicYear("2025-2026")
            .build();

    assertDoesNotThrow(() -> validator.validate(request));
  }

  @Test
  void rejects_course_teaching_assignment_without_academic_year() {
    var request =
        AssignCourseTeachingRequest.builder().courseId(1L).teacherId(2L).groupId(3L).build();

    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Academic year");
  }
}
