package api.poja.app.repository;

import api.poja.app.jpa.StudentGroupAssignmentEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGroupAssignmentRepository
    extends JpaRepository<StudentGroupAssignmentEntity, Long> {
  List<StudentGroupAssignmentEntity> findByStudentIdOrderByStartDateDesc(Long studentId);

  Optional<StudentGroupAssignmentEntity> findByStudentIdAndEndDateIsNull(Long studentId);

  List<StudentGroupAssignmentEntity> findByGroupIdAndEndDateIsNull(Long groupId);
}
