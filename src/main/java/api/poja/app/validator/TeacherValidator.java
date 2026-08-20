package api.poja.app.validator;

import api.poja.app.dto.request.CreateTeacherRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class TeacherValidator {
  public void validate(CreateTeacherRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getFirstName() == null || request.getFirstName().isBlank()) {
      throw new ValidationException("Teacher first name is required");
    }
    if (request.getLastName() == null || request.getLastName().isBlank()) {
      throw new ValidationException("Teacher last name is required");
    }
    if (request.getEmail() == null || !request.getEmail().contains("@")) {
      throw new ValidationException("Teacher email is invalid");
    }
  }
}
