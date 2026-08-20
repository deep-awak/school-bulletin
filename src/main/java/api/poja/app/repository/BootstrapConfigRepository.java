package api.poja.app.repository;

import api.poja.app.jpa.BootstrapConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BootstrapConfigRepository extends JpaRepository<BootstrapConfigEntity, UUID> {
	Optional<BootstrapConfigEntity> findByKey(String key);
	boolean existsByKey(String key);
}