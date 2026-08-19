package api.poja.app.security;

import api.poja.app.exception.ForbiddenException;
import api.poja.app.repository.CourseTeachingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/** Central place enforcing the STUDENT / TEACHER / ADMIN permission rules. */
@Component
@AllArgsConstructor
public class AccessGuard {

  private final CourseTeachingRepository courseTeachingRepository;

  public void requireAdmin(CurrentUser user) {
    if (!user.isAdmin()) {
      throw new ForbiddenException("This action requires the ADMIN role");
    }
  }

  /** A student may only access their own data; teachers and admins are unrestricted here. */
  public void requireSelfOrStaff(CurrentUser user, Long studentId) {
    if (user.isAdmin() || user.isTeacher()) {
      return;
    }
    if (user.isStudent() && studentId != null && studentId.equals(user.getStudentId())) {
      return;
    }
    throw new ForbiddenException("You can only access your own data");
  }

  /**
   * A teacher may only manage grades for a course they are actually assigned to teach, for the
   * student's current group. Admins bypass this check.
   */
  public void requireCourseTeacherOrAdmin(
      CurrentUser user, Long courseId, Long teacherGroupId) {
    if (user.isAdmin()) {
      return;
    }
    if (!user.isTeacher() || user.getTeacherId() == null) {
      throw new ForbiddenException("Only a teacher or an admin can modify grades");
    }
    boolean isAssigned =
        courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(
            user.getTeacherId(), courseId, teacherGroupId);
    if (!isAssigned) {
      throw new ForbiddenException(
          "You are not assigned to teach this course to this student's group");
    }
  }
}
