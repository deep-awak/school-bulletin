package api.poja.app.mapper;

import api.poja.app.dto.GroupDto;
import api.poja.app.jpa.GroupEntity;
import api.poja.app.model.Group;

public final class GroupMapper {
  private GroupMapper() {}

  public static Group toModel(GroupEntity e) {
    if (e == null) return null;
    return Group.builder()
        .id(e.getId())
        .name(e.getName())
        .promotionId(e.getPromotion() != null ? e.getPromotion().getId() : null)
        .build();
  }

  public static GroupDto toDto(Group m) {
    if (m == null) return null;
    return GroupDto.builder()
        .id(m.getId())
        .name(m.getName())
        .promotionId(m.getPromotionId())
        .build();
  }
}
