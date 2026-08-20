package api.poja.app.repository;

import api.poja.app.jpa.TeacherEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<TeacherEntity, String> {
  Optional<TeacherEntity> findByUserId(UUID userId);
}
