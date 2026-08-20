package api.poja.app.repository;

import api.poja.app.jpa.GradeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
  List<GradeEntity> findByStudentId(Long studentId);

  List<GradeEntity> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

  List<GradeEntity> findByCourseId(Long courseId);

  List<GradeEntity> findByStudentIdIn(List<Long> studentIds);
}
