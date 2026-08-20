package api.poja.app.unit.validator;

import api.poja.app.dto.request.CreateStudentRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.StudentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StudentValidatorTest {

	private StudentValidator validator;
	private CreateStudentRequest validRequest;

	@BeforeEach
	void setUp() {
		validator = new StudentValidator();
		validRequest = CreateStudentRequest.builder()
				.std("HEI-2023-014")
				.firstName("Jean")
				.lastName("Rakoto")
				.email("jean.rakoto@hei.mg")
				.promotionId(UUID.randomUUID())
				.groupId("J1")
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
	void validate_shouldThrowWhenStdIsNull() {
		validRequest.setStd(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student STD number is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenStdIsBlank() {
		validRequest.setStd("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student STD number is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenFirstNameIsNull() {
		validRequest.setFirstName(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student first name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenFirstNameIsBlank() {
		validRequest.setFirstName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student first name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenLastNameIsNull() {
		validRequest.setLastName(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student last name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenLastNameIsBlank() {
		validRequest.setLastName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student last name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenEmailIsNull() {
		validRequest.setEmail(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student email is invalid", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenEmailDoesNotContainAt() {
		validRequest.setEmail("jean.rakotohei.mg");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student email is invalid", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenPromotionIdIsNull() {
		validRequest.setPromotionId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student must be attached to a promotion", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenGroupIdIsNull() {
		validRequest.setGroupId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Student must be attached to a group", ex.getMessage());
	}
}