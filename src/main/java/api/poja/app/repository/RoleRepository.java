package api.poja.app.repository;

import api.poja.app.jpa.RoleEntity;
import api.poja.app.model.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
  Optional<RoleEntity> findByName(Role name);
}
