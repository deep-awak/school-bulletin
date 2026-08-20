package api.poja.app.validator;

import api.poja.app.dto.request.CreatePromotionRequest;
import api.poja.app.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PromotionValidator {
  public void validate(CreatePromotionRequest request) {
    if (request == null) {
      throw new ValidationException("Request must not be null");
    }
    if (request.getName() == null || request.getName().isBlank()) {
      throw new ValidationException("Promotion name is required");
    }
    if (request.getStartYear() == null) {
      throw new ValidationException("Promotion start year is required");
    }
    if (request.getEndYear() == null) {
      throw new ValidationException("Promotion end year is required");
    }
    if (request.getEndYear() < request.getStartYear()) {
      throw new ValidationException("Promotion end year must be after start year");
    }
  }
}
