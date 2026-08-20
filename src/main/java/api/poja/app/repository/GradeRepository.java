package api.poja.app.repository;

import api.poja.app.jpa.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<GradeEntity, UUID> {
  List<GradeEntity> findByStudentId(String studentId);
  List<GradeEntity> findByStudentIdAndAcademicYear(String studentId, String academicYear);
  List<GradeEntity> findByStudentIdIn(List<String> studentIds);
}