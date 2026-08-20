package api.poja.app.repository;

import api.poja.app.jpa.GradeHistoryEntity;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeHistoryRepository extends JpaRepository<GradeHistoryEntity, UUID> {
  List<GradeHistoryEntity> findByGradeIdOrderByChangedAtDesc(UUID gradeId);
}
