package api.poja.app.mapper;

import api.poja.app.dto.PromotionDto;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.model.Promotion;

public final class PromotionMapper {
  private PromotionMapper() {}

  public static Promotion toModel(PromotionEntity e) {
    if (e == null) return null;
    return Promotion.builder()
        .id(e.getId())
        .name(e.getName())
        .startYear(e.getStartYear())
        .endYear(e.getEndYear())
        .build();
  }

  public static PromotionEntity toEntity(Promotion m) {
    if (m == null) return null;
    return PromotionEntity.builder()
        .id(m.getId())
        .name(m.getName())
        .startYear(m.getStartYear())
        .endYear(m.getEndYear())
        .build();
  }

  public static PromotionDto toDto(Promotion m) {
    if (m == null) return null;
    return PromotionDto.builder()
        .id(m.getId())
        .name(m.getName())
        .startYear(m.getStartYear())
        .endYear(m.getEndYear())
        .build();
  }
}
