package api.poja.app.repository;

import api.poja.app.jpa.StudentGroupAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentGroupAssignmentRepository extends JpaRepository<StudentGroupAssignmentEntity, UUID> {

  List<StudentGroupAssignmentEntity> findByStudentIdOrderByStartDateDesc(String studentId);

  Optional<StudentGroupAssignmentEntity> findByStudentIdAndEndDateIsNull(String studentId);
}