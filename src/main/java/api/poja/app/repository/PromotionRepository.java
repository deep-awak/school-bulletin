package api.poja.app.repository;

import api.poja.app.jpa.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PromotionRepository extends JpaRepository<PromotionEntity, UUID> {}
