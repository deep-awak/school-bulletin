package api.poja.app.validator;

import api.poja.app.dto.request.CreateCourseRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CourseValidator {
  public void validate(CreateCourseRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getName() == null || request.getName().isBlank()) {
      throw new ValidationException("Course name is required");
    }
    if (request.getCode() == null || request.getCode().isBlank()) {
      throw new ValidationException("Course code is required");
    }
  }
}
