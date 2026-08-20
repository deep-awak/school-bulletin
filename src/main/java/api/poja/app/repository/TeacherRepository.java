package api.poja.app.repository;

import api.poja.app.jpa.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<TeacherEntity, String> {
  Optional<TeacherEntity> findByUserId(UUID userId);
}