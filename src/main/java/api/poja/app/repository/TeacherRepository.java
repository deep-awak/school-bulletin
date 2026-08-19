package api.poja.app.repository;

import api.poja.app.jpa.TeacherEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {
  Optional<TeacherEntity> findByUserId(Long userId);
}
