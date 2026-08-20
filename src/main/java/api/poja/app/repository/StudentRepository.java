package api.poja.app.repository;

import api.poja.app.jpa.StudentEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
  List<StudentEntity> findByPromotionId(Long promotionId);

  Optional<StudentEntity> findByStd(String std);

  Optional<StudentEntity> findByUserId(Long userId);
}
