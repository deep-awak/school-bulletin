package api.poja.app.unit.security;

import api.poja.app.exception.ForbiddenException;
import api.poja.app.model.Role;
import api.poja.app.repository.CourseTeachingRepository;
import api.poja.app.security.AccessGuard;
import api.poja.app.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessGuardTest {

	@Mock
	private CourseTeachingRepository courseTeachingRepository;

	private AccessGuard accessGuard;
	private CurrentUser adminUser;
	private CurrentUser teacherUser;
	private CurrentUser studentUser;

	@BeforeEach
	void setUp() {
		accessGuard = new AccessGuard(courseTeachingRepository);

		adminUser = CurrentUser.builder()
				.userId(UUID.randomUUID())
				.role(Role.ADMIN)
				.build();

		teacherUser = CurrentUser.builder()
				.userId(UUID.randomUUID())
				.role(Role.TEACHER)
				.teacherId("TCH24001")
				.build();

		studentUser = CurrentUser.builder()
				.userId(UUID.randomUUID())
				.role(Role.STUDENT)
				.studentId("STD24185")
				.build();
	}

	@Test
	void requireAdmin_shouldNotThrowWhenUserIsAdmin() {
		assertDoesNotThrow(() -> accessGuard.requireAdmin(adminUser));
	}

	@Test
	void requireAdmin_shouldThrowWhenUserIsNotAdmin() {
		ForbiddenException ex = assertThrows(ForbiddenException.class,
				() -> accessGuard.requireAdmin(teacherUser));
		assertEquals("This action requires the ADMIN role", ex.getMessage());
	}

	@Test
	void requireSelfOrStaff_shouldNotThrowWhenUserIsAdmin() {
		assertDoesNotThrow(() -> accessGuard.requireSelfOrStaff(adminUser, "STD24185"));
	}

	@Test
	void requireSelfOrStaff_shouldNotThrowWhenUserIsTeacher() {
		assertDoesNotThrow(() -> accessGuard.requireSelfOrStaff(teacherUser, "STD24185"));
	}

	@Test
	void requireSelfOrStaff_shouldNotThrowWhenStudentAccessesOwnData() {
		assertDoesNotThrow(() -> accessGuard.requireSelfOrStaff(studentUser, "STD24185"));
	}

	@Test
	void requireSelfOrStaff_shouldThrowWhenStudentAccessesOtherData() {
		ForbiddenException ex = assertThrows(ForbiddenException.class,
				() -> accessGuard.requireSelfOrStaff(studentUser, "STD24186"));
		assertEquals("You can only access your own data", ex.getMessage());
	}

	@Test
	void requireSelfOrStaff_shouldThrowWhenStudentIdIsNull() {
		ForbiddenException ex = assertThrows(ForbiddenException.class,
				() -> accessGuard.requireSelfOrStaff(studentUser, null));
		assertEquals("You can only access your own data", ex.getMessage());
	}

	@Test
	void requireCourseTeacherOrAdmin_shouldNotThrowWhenUserIsAdmin() {
		assertDoesNotThrow(() -> accessGuard.requireCourseTeacherOrAdmin(
				adminUser, UUID.randomUUID(), "J1"));
	}

	@Test
	void requireCourseTeacherOrAdmin_shouldThrowWhenUserIsStudent() {
		ForbiddenException ex = assertThrows(ForbiddenException.class,
				() -> accessGuard.requireCourseTeacherOrAdmin(
						studentUser, UUID.randomUUID(), "J1"));
		assertEquals("Only a teacher or an admin can modify grades", ex.getMessage());
	}

	@Test
	void requireCourseTeacherOrAdmin_shouldThrowWhenTeacherIdIsNull() {
		CurrentUser teacherWithoutId = CurrentUser.builder()
				.userId(UUID.randomUUID())
				.role(Role.TEACHER)
				.teacherId(null)
				.build();

		ForbiddenException ex = assertThrows(ForbiddenException.class,
				() -> accessGuard.requireCourseTeacherOrAdmin(
						teacherWithoutId, UUID.randomUUID(), "J1"));
		assertEquals("Only a teacher or an admin can modify grades", ex.getMessage());
	}

	@Test
	void requireCourseTeacherOrAdmin_shouldNotThrowWhenTeacherIsAssigned() {
		when(courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(
				any(), any(), any())).thenReturn(true);

		assertDoesNotThrow(() -> accessGuard.requireCourseTeacherOrAdmin(
				teacherUser, UUID.randomUUID(), "J1"));
	}

	@Test
	void requireCourseTeacherOrAdmin_shouldThrowWhenTeacherIsNotAssigned() {
		when(courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(
				any(), any(), any())).thenReturn(false);

		ForbiddenException ex = assertThrows(ForbiddenException.class,
				() -> accessGuard.requireCourseTeacherOrAdmin(
						teacherUser, UUID.randomUUID(), "J1"));
		assertEquals("You are not assigned to teach this course to this student's group", ex.getMessage());
	}
}