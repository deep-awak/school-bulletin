package api.poja.app.validator;

import api.poja.app.dto.request.CreateGroupRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class GroupValidator {
  public void validate(CreateGroupRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getName() == null || request.getName().isBlank()) {
      throw new ValidationException("Group name is required");
    }
    if (request.getPromotionId() == null) {
      throw new ValidationException("Group must be attached to a promotion");
    }
  }
}
