package api.poja.app.unit.validator;

import api.poja.app.dto.request.AssignCourseTeachingRequest;
import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.AssignmentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentValidatorTest {

	private AssignmentValidator validator;
	private AssignStudentGroupRequest validStudentGroupRequest;
	private AssignCourseTeachingRequest validCourseTeachingRequest;

	@BeforeEach
	void setUp() {
		validator = new AssignmentValidator();

		validStudentGroupRequest = AssignStudentGroupRequest.builder()
				.studentId("STD24185")
				.groupId("J1")
				.startDate(LocalDate.now())
				.build();

		validCourseTeachingRequest = AssignCourseTeachingRequest.builder()
				.courseId(UUID.randomUUID())
				.teacherId("TCH24001")
				.groupId("J1")
				.academicYear("2025-2026")
				.build();
	}

	@Test
	void validateStudentGroup_shouldNotThrowWhenRequestIsValid() {
		assertDoesNotThrow(() -> validator.validate(validStudentGroupRequest));
	}

	@Test
	void validateStudentGroup_shouldThrowWhenRequestIsNull() {
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate((AssignStudentGroupRequest) null));
		assertEquals("Request must not be null", ex.getMessage());
	}

	@Test
	void validateStudentGroup_shouldThrowWhenStudentIdIsNull() {
		validStudentGroupRequest.setStudentId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validStudentGroupRequest));
		assertEquals("Student id is required", ex.getMessage());
	}

	@Test
	void validateStudentGroup_shouldThrowWhenGroupIdIsNull() {
		validStudentGroupRequest.setGroupId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validStudentGroupRequest));
		assertEquals("Group id is required", ex.getMessage());
	}

	@Test
	void validateStudentGroup_shouldThrowWhenStartDateIsNull() {
		validStudentGroupRequest.setStartDate(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validStudentGroupRequest));
		assertEquals("Start date is required", ex.getMessage());
	}

	@Test
	void validateCourseTeaching_shouldNotThrowWhenRequestIsValid() {
		assertDoesNotThrow(() -> validator.validate(validCourseTeachingRequest));
	}

	@Test
	void validateCourseTeaching_shouldThrowWhenRequestIsNull() {
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate((AssignCourseTeachingRequest) null));
		assertEquals("Request must not be null", ex.getMessage());
	}

	@Test
	void validateCourseTeaching_shouldThrowWhenCourseIdIsNull() {
		validCourseTeachingRequest.setCourseId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCourseTeachingRequest));
		assertEquals("Course id is required", ex.getMessage());
	}

	@Test
	void validateCourseTeaching_shouldThrowWhenTeacherIdIsNull() {
		validCourseTeachingRequest.setTeacherId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCourseTeachingRequest));
		assertEquals("Teacher id is required", ex.getMessage());
	}

	@Test
	void validateCourseTeaching_shouldThrowWhenGroupIdIsNull() {
		validCourseTeachingRequest.setGroupId(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCourseTeachingRequest));
		assertEquals("Group id is required", ex.getMessage());
	}

	@Test
	void validateCourseTeaching_shouldThrowWhenAcademicYearIsNull() {
		validCourseTeachingRequest.setAcademicYear(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCourseTeachingRequest));
		assertEquals("Academic year is required", ex.getMessage());
	}

	@Test
	void validateCourseTeaching_shouldThrowWhenAcademicYearIsBlank() {
		validCourseTeachingRequest.setAcademicYear("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validCourseTeachingRequest));
		assertEquals("Academic year is required", ex.getMessage());
	}
}