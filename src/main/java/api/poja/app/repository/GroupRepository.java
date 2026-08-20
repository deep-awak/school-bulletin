package api.poja.app.repository;

import api.poja.app.jpa.GroupEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
  List<GroupEntity> findByPromotionId(Long promotionId);
}
