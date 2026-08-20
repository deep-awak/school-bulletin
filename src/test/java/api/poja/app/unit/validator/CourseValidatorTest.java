package api.poja.app.unit.validator;

import api.poja.app.dto.request.CreateCourseRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.CourseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseValidatorTest {

	private CourseValidator validator;
	private CreateCourseRequest validRequest;

	@BeforeEach
	void setUp() {
		validator = new CourseValidator();
		validRequest = CreateCourseRequest.builder()
				.name("Algorithmique")
				.code("ALG1")
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
		assertEquals("Course name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenNameIsBlank() {
		validRequest.setName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Course name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenCodeIsNull() {
		validRequest.setCode(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Course code is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenCodeIsBlank() {
		validRequest.setCode("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Course code is required", ex.getMessage());
	}
}