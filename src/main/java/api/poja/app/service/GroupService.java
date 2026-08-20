package api.poja.app.service;

import api.poja.app.dto.request.CreateGroupRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.GroupEntity;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.mapper.GroupMapper;
import api.poja.app.model.Group;
import api.poja.app.repository.GroupRepository;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.validator.GroupValidator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {
  private final GroupRepository groupRepository;
  private final PromotionRepository promotionRepository;
  private final GroupValidator groupValidator;

  public Group create(CreateGroupRequest request) {
    groupValidator.validate(request);
    PromotionEntity promotion =
        promotionRepository
            .findById(request.getPromotionId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Promotion not found: " + request.getPromotionId()));
    GroupEntity entity =
        GroupEntity.builder().name(request.getName()).promotion(promotion).build();
    return GroupMapper.toModel(groupRepository.save(entity));
  }

  public Group getById(Long id) {
    return GroupMapper.toModel(
        groupRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + id)));
  }

  public List<Group> listByPromotion(Long promotionId) {
    return groupRepository.findByPromotionId(promotionId).stream()
        .map(GroupMapper::toModel)
        .toList();
  }
}
