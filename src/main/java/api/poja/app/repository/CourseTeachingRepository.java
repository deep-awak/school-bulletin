package api.poja.app.repository;

import api.poja.app.jpa.CourseTeachingEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTeachingRepository extends JpaRepository<CourseTeachingEntity, UUID> {
  List<CourseTeachingEntity> findByTeacherId(String teacherId);

  boolean existsByTeacherIdAndCourseIdAndGroupId(String teacherId, UUID courseId, String groupId);
}
