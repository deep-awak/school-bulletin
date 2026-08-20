package api.poja.app.unit.validator;

import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.GradeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GradeValidatorTest {

	private GradeValidator validator;
	private CreateGradeRequest validCreateRequest;
	private UpdateGradeRequest validUpdateRequest;

	@BeforeEach
	void setUp() {
		validator = new GradeValidator();
		validCreateRequest = CreateGradeRequest.builder()
				.studentId("STD24185")
				.courseId(UUID.randomUUID())
				.academicYear("2025-2026")
				.value(14.5)
				.build();

		validUpdateRequest = UpdateGradeRequest.builder()
				.value(16.0)
				.reason("Correction after student complaint")
				.build();
	}

	@Test
	void validateCreate_shouldNotThrowWhenRequestIsValid() {
		assertDoesNotThrow(() -> validator.validate(validCreateRequest));
	}

	@Test
	void validateCreate_shouldThrowWhenRequestIsNull() {
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate((CreateGradeRequest) null));
		assertEquals("Request must not be null", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenStudentIdIsNull() {
		validCreateRequest.setStudentId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Student id is required", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenCourseIdIsNull() {
		validCreateRequest.setCourseId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Course id is required", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenAcademicYearIsNull() {
		validCreateRequest.setAcademicYear(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Academic year is required", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenAcademicYearIsBlank() {
		validCreateRequest.setAcademicYear("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Academic year is required", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenValueIsNull() {
		validCreateRequest.setValue(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Grade value is required", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenValueIsLessThanZero() {
		validCreateRequest.setValue(-1.0);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Grade must be between 0.0 and 20.0", ex.getMessage());
	}

	@Test
	void validateCreate_shouldThrowWhenValueIsGreaterThanTwenty() {
		validCreateRequest.setValue(20.5);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCreateRequest));
		assertEquals("Grade must be between 0.0 and 20.0", ex.getMessage());
	}

	@Test
	void validateUpdate_shouldNotThrowWhenRequestIsValid() {
		assertDoesNotThrow(() -> validator.validate(validUpdateRequest));
	}

	@Test
	void validateUpdate_shouldThrowWhenRequestIsNull() {
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate((UpdateGradeRequest) null));
		assertEquals("Request must not be null", ex.getMessage());
	}

	@Test
	void validateUpdate_shouldThrowWhenValueIsNull() {
		validUpdateRequest.setValue(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validUpdateRequest));
		assertEquals("Grade value is required", ex.getMessage());
	}

	@Test
	void validateUpdate_shouldThrowWhenValueIsLessThanZero() {
		validUpdateRequest.setValue(-1.0);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validUpdateRequest));
		assertEquals("Grade must be between 0.0 and 20.0", ex.getMessage());
	}

	@Test
	void validateUpdate_shouldThrowWhenValueIsGreaterThanTwenty() {
		validUpdateRequest.setValue(20.5);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validUpdateRequest));
		assertEquals("Grade must be between 0.0 and 20.0", ex.getMessage());
	}

	@Test
	void validateUpdate_shouldThrowWhenReasonIsNull() {
		validUpdateRequest.setReason(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validUpdateRequest));
		assertEquals("A reason is required to modify a grade", ex.getMessage());
	}

	@Test
	void validateUpdate_shouldThrowWhenReasonIsBlank() {
		validUpdateRequest.setReason("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validUpdateRequest));
		assertEquals("A reason is required to modify a grade", ex.getMessage());
	}
}