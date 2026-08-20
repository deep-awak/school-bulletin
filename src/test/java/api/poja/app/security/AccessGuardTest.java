package api.poja.app.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import api.poja.app.exception.ForbiddenException;
import api.poja.app.model.Role;
import api.poja.app.repository.CourseTeachingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccessGuardTest {

  @Mock private CourseTeachingRepository courseTeachingRepository;

  private AccessGuard accessGuard;

  @BeforeEach
  void setUp() {
    accessGuard = new AccessGuard(courseTeachingRepository);
  }

  private CurrentUser user(Role role, Long studentId, Long teacherId) {
    return CurrentUser.builder()
        .userId(99L)
        .role(role)
        .studentId(studentId)
        .teacherId(teacherId)
        .build();
  }

  @Test
  void admin_only_action_is_allowed_for_admin() {
    assertThatCode(() -> accessGuard.requireAdmin(user(Role.ADMIN, null, null)))
        .doesNotThrowAnyException();
  }

  @Test
  void admin_only_action_is_forbidden_for_teacher() {
    assertThatThrownBy(() -> accessGuard.requireAdmin(user(Role.TEACHER, null, 5L)))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void a_student_can_access_their_own_data() {
    var student = user(Role.STUDENT, 42L, null);
    assertThatCode(() -> accessGuard.requireSelfOrStaff(student, 42L)).doesNotThrowAnyException();
  }

  @Test
  void a_student_cannot_access_another_students_data() {
    var student = user(Role.STUDENT, 42L, null);
    assertThatThrownBy(() -> accessGuard.requireSelfOrStaff(student, 43L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void a_teacher_can_access_any_student_data() {
    var teacher = user(Role.TEACHER, null, 7L);
    assertThatCode(() -> accessGuard.requireSelfOrStaff(teacher, 43L)).doesNotThrowAnyException();
  }

  @Test
  void a_teacher_assigned_to_the_course_and_group_can_modify_grades() {
    var teacher = user(Role.TEACHER, null, 7L);
    when(courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(7L, 10L, 3L))
        .thenReturn(true);

    assertThatCode(() -> accessGuard.requireCourseTeacherOrAdmin(teacher, 10L, 3L))
        .doesNotThrowAnyException();
  }

  @Test
  void a_teacher_not_assigned_to_the_course_cannot_modify_grades() {
    var teacher = user(Role.TEACHER, null, 7L);
    when(courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(7L, 10L, 3L))
        .thenReturn(false);

    assertThatThrownBy(() -> accessGuard.requireCourseTeacherOrAdmin(teacher, 10L, 3L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void a_student_can_never_modify_grades() {
    var student = user(Role.STUDENT, 42L, null);

    assertThatThrownBy(() -> accessGuard.requireCourseTeacherOrAdmin(student, 10L, 3L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void admin_can_modify_grades_for_any_course() {
    var admin = user(Role.ADMIN, null, null);

    assertThatCode(() -> accessGuard.requireCourseTeacherOrAdmin(admin, 10L, 3L))
        .doesNotThrowAnyException();
  }
}
