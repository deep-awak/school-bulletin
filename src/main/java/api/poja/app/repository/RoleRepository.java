package api.poja.app.repository;

import api.poja.app.jpa.RoleEntity;
import api.poja.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
  Optional<RoleEntity> findByName(Role name);
}