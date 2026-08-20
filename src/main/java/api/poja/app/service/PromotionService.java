package api.poja.app.service;

import api.poja.app.dto.request.CreatePromotionRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.mapper.PromotionMapper;
import api.poja.app.model.Promotion;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.validator.PromotionValidator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PromotionService {
  private final PromotionRepository promotionRepository;
  private final PromotionValidator promotionValidator;

  public Promotion create(CreatePromotionRequest request) {
    promotionValidator.validate(request);
    PromotionEntity entity =
        PromotionEntity.builder()
            .name(request.getName())
            .startYear(request.getStartYear())
            .endYear(request.getEndYear())
            .build();
    return PromotionMapper.toModel(promotionRepository.save(entity));
  }

  public Promotion getById(UUID id) {
    return PromotionMapper.toModel(
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found: " + id)));
  }

  public List<Promotion> listAll() {
    return promotionRepository.findAll().stream().map(PromotionMapper::toModel).toList();
  }
}
