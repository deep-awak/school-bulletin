package api.poja.app.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.exception.ValidationException;
import org.junit.jupiter.api.Test;

class GradeValidatorTest {

  private final GradeValidator validator = new GradeValidator();

  @Test
  void accepts_a_valid_create_request() {
    var request =
        CreateGradeRequest.builder()
            .studentId(1L)
            .courseId(2L)
            .academicYear("2025-2026")
            .value(15.5)
            .build();

    assertDoesNotThrow(() -> validator.validate(request));
  }

  @Test
  void rejects_null_create_request() {
    assertThatThrownBy(() -> validator.validate((CreateGradeRequest) null))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void rejects_create_request_missing_student() {
    var request =
        CreateGradeRequest.builder().courseId(2L).academicYear("2025-2026").value(10d).build();

    assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(ValidationException.class);
  }

  @Test
  void rejects_grade_value_above_twenty() {
    var request =
        CreateGradeRequest.builder()
            .studentId(1L)
            .courseId(2L)
            .academicYear("2025-2026")
            .value(20.5)
            .build();

    assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(ValidationException.class);
  }

  @Test
  void rejects_negative_grade_value() {
    var request =
        CreateGradeRequest.builder()
            .studentId(1L)
            .courseId(2L)
            .academicYear("2025-2026")
            .value(-1d)
            .build();

    assertThatThrownBy(() -> validator.validate(request)).isInstanceOf(ValidationException.class);
  }

  @Test
  void update_requires_a_non_blank_reason() {
    var request = UpdateGradeRequest.builder().value(12d).reason("  ").build();

    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("reason");
  }

  @Test
  void update_accepts_valid_value_and_reason() {
    var request = UpdateGradeRequest.builder().value(12d).reason("Grading error").build();

    assertDoesNotThrow(() -> validator.validate(request));
  }
}
