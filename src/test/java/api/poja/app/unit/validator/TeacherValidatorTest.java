package api.poja.app.unit.validator;

import api.poja.app.dto.request.CreateTeacherRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.TeacherValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeacherValidatorTest {

	private TeacherValidator validator;
	private CreateTeacherRequest validRequest;

	@BeforeEach
	void setUp() {
		validator = new TeacherValidator();
		validRequest = CreateTeacherRequest.builder()
				.firstName("Herimanana")
				.lastName("Rabe")
				.email("herimanana.rabe@hei.mg")
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
	void validate_shouldThrowWhenFirstNameIsNull() {
		validRequest.setFirstName(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Teacher first name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenFirstNameIsBlank() {
		validRequest.setFirstName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Teacher first name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenLastNameIsNull() {
		validRequest.setLastName(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Teacher last name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenLastNameIsBlank() {
		validRequest.setLastName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Teacher last name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenEmailIsNull() {
		validRequest.setEmail(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Teacher email is invalid", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenEmailDoesNotContainAt() {
		validRequest.setEmail("herimanana.rabehei.mg");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Teacher email is invalid", ex.getMessage());
	}
}