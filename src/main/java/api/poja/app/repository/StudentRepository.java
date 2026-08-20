package api.poja.app.repository;

import api.poja.app.jpa.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<StudentEntity, String> {
  List<StudentEntity> findByPromotionId(UUID promotionId);
}