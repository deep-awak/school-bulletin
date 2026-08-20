package api.poja.app.repository;

import api.poja.app.jpa.GroupEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, String> {
  List<GroupEntity> findByPromotionId(UUID promotionId);
}
