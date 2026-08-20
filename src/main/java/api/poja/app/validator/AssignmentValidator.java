package api.poja.app.validator;

import api.poja.app.dto.request.AssignCourseTeachingRequest;
import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AssignmentValidator {

  public void validate(AssignStudentGroupRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getStudentId() == null) {
      throw new ValidationException("Student id is required");
    }
    if (request.getGroupId() == null) {
      throw new ValidationException("Group id is required");
    }
    if (request.getStartDate() == null) {
      throw new ValidationException("Start date is required");
    }
  }

  public void validate(AssignCourseTeachingRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getCourseId() == null) {
      throw new ValidationException("Course id is required");
    }
    if (request.getTeacherId() == null) {
      throw new ValidationException("Teacher id is required");
    }
    if (request.getGroupId() == null) {
      throw new ValidationException("Group id is required");
    }
    if (request.getAcademicYear() == null || request.getAcademicYear().isBlank()) {
      throw new ValidationException("Academic year is required");
    }
  }
}
