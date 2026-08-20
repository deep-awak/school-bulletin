package api.poja.app.validator;

import api.poja.app.dto.request.CreateStudentRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class StudentValidator {
  public void validate(CreateStudentRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getStd() == null || request.getStd().isBlank()) {
      throw new ValidationException("Student STD number is required");
    }
    if (request.getFirstName() == null || request.getFirstName().isBlank()) {
      throw new ValidationException("Student first name is required");
    }
    if (request.getLastName() == null || request.getLastName().isBlank()) {
      throw new ValidationException("Student last name is required");
    }
    if (request.getEmail() == null || !request.getEmail().contains("@")) {
      throw new ValidationException("Student email is invalid");
    }
    if (request.getPromotionId() == null) {
      throw new ValidationException("Student must be attached to a promotion");
    }
    if (request.getGroupId() == null) {
      throw new ValidationException("Student must be attached to a group");
    }
  }
}
