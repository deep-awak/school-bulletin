package api.poja.app.validator;

import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class GradeValidator {

  private static final double MIN_GRADE = 0d;
  private static final double MAX_GRADE = 20d;

  public void validate(CreateGradeRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getStudentId() == null) {
      throw new ValidationException("Student id is required");
    }
    if (request.getCourseId() == null) {
      throw new ValidationException("Course id is required");
    }
    if (request.getAcademicYear() == null || request.getAcademicYear().isBlank()) {
      throw new ValidationException("Academic year is required");
    }
    validateValue(request.getValue());
  }

  public void validate(UpdateGradeRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    validateValue(request.getValue());
    if (request.getReason() == null || request.getReason().isBlank()) {
      throw new ValidationException("A reason is required to modify a grade");
    }
  }

  private void validateValue(Double value) {
    if (value == null) {
      throw new ValidationException("Grade value is required");
    }
    if (value < MIN_GRADE || value > MAX_GRADE) {
      throw new ValidationException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
    }
  }
}
