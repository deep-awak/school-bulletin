package api.poja.app.repository;

import api.poja.app.jpa.BootstrapConfigEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BootstrapConfigRepository extends JpaRepository<BootstrapConfigEntity, UUID> {
  Optional<BootstrapConfigEntity> findByKey(String key);

  boolean existsByKey(String key);
}
