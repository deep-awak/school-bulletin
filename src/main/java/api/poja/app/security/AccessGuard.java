package api.poja.app.security;

import api.poja.app.exception.ForbiddenException;
import api.poja.app.repository.CourseTeachingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessGuard {

  private final CourseTeachingRepository courseTeachingRepository;

  public void requireAdmin(CurrentUser user) {
    if (!user.isAdmin()) {
      throw new ForbiddenException("This action requires the ADMIN role");
    }
  }

  public void requireSelfOrStaff(CurrentUser user, String studentId) {
    if (user.isAdmin() || user.isTeacher()) {
      return;
    }
    if (user.isStudent() && studentId != null && studentId.equals(user.getStudentId())) {
      return;
    }
    throw new ForbiddenException("You can only access your own data");
  }

  public void requireCourseTeacherOrAdmin(CurrentUser user, UUID courseId, String teacherGroupId) {
    if (user.isAdmin()) {
      return;
    }
    if (!user.isTeacher() || user.getTeacherId() == null) {
      throw new ForbiddenException("Only a teacher or an admin can modify grades");
    }
    boolean isAssigned = courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(
            user.getTeacherId(), courseId, teacherGroupId);
    if (!isAssigned) {
      throw new ForbiddenException("You are not assigned to teach this course to this student's group");
    }
  }
}