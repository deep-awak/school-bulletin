package api.poja.app.repository;

import api.poja.app.jpa.PromotionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<PromotionEntity, UUID> {}
