package api.poja.app.repository;

import api.poja.app.jpa.GradeHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeHistoryRepository extends JpaRepository<GradeHistoryEntity, Long> {
  List<GradeHistoryEntity> findByGradeIdOrderByChangedAtDesc(Long gradeId);
}
