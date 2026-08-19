package api.poja.app.repository;

import api.poja.app.jpa.CourseTeachingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTeachingRepository extends JpaRepository<CourseTeachingEntity, Long> {
  List<CourseTeachingEntity> findByTeacherId(Long teacherId);

  List<CourseTeachingEntity> findByCourseIdAndTeacherIdAndGroupId(
      Long courseId, Long teacherId, Long groupId);

  boolean existsByTeacherIdAndCourseIdAndGroupId(Long teacherId, Long courseId, Long groupId);
}
