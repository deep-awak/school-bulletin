package api.poja.app.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import api.poja.app.dto.request.CreateStudentRequest;
import api.poja.app.exception.ValidationException;
import org.junit.jupiter.api.Test;

class StudentValidatorTest {

  private final StudentValidator validator = new StudentValidator();

  private CreateStudentRequest.CreateStudentRequestBuilder validRequest() {
    return CreateStudentRequest.builder()
        .std("HEI-2023-014")
        .firstName("Jean")
        .lastName("Rakoto")
        .email("jean.rakoto@hei.mg")
        .promotionId(1L)
        .groupId(2L);
  }

  @Test
  void accepts_a_complete_valid_request() {
    assertDoesNotThrow(() -> validator.validate(validRequest().build()));
  }

  @Test
  void rejects_null_request() {
    assertThatThrownBy(() -> validator.validate(null)).isInstanceOf(ValidationException.class);
  }

  @Test
  void rejects_blank_std() {
    var request = validRequest().std("  ").build();
    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("STD");
  }

  @Test
  void rejects_email_without_at_sign() {
    var request = validRequest().email("not-an-email").build();
    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("email");
  }

  @Test
  void rejects_missing_promotion() {
    var request = validRequest().promotionId(null).build();
    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("promotion");
  }

  @Test
  void rejects_missing_group() {
    var request = validRequest().groupId(null).build();
    assertThatThrownBy(() -> validator.validate(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("group");
  }
}
