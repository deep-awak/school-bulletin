package api.poja.app.repository;

import api.poja.app.jpa.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRepository extends JpaRepository<CourseEntity, UUID> {
}