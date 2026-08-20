package api.poja.app.unit.validator;

import api.poja.app.dto.request.CreateGroupRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.GroupValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupValidatorTest {

	private GroupValidator validator;
	private CreateGroupRequest validRequest;

	@BeforeEach
	void setUp() {
		validator = new GroupValidator();
		validRequest = CreateGroupRequest.builder()
				.name("Groupe A1")
				.promotionId(UUID.randomUUID())
				.build();
	}

	@Test
	void validate_shouldNotThrowWhenRequestIsValid() {
		assertDoesNotThrow(() -> validator.validate(validRequest));
	}

	@Test
	void validate_shouldThrowWhenRequestIsNull() {
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(null));
		assertEquals("Request must not be null", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenNameIsNull() {
		validRequest.setName(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Group name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenNameIsBlank() {
		validRequest.setName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Group name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenPromotionIdIsNull() {
		validRequest.setPromotionId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Group must be attached to a promotion", ex.getMessage());
	}
}