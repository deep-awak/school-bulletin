package api.poja.app.mapper;

import api.poja.app.dto.StudentDto;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.model.Student;

public final class StudentMapper {
  private StudentMapper() {}

  public static Student toModel(StudentEntity e) {
    if (e == null) return null;
    return Student.builder()
        .id(e.getId())
        .std(e.getStd())
        .firstName(e.getFirstName())
        .lastName(e.getLastName())
        .email(e.getEmail())
        .promotionId(e.getPromotion() != null ? e.getPromotion().getId() : null)
        .userId(e.getUserId())
        .build();
  }

  public static StudentEntity toEntity(Student m, PromotionEntity promotion) {
    if (m == null) return null;
    return StudentEntity.builder()
        .id(m.getId())
        .std(m.getStd())
        .firstName(m.getFirstName())
        .lastName(m.getLastName())
        .email(m.getEmail())
        .promotion(promotion)
        .userId(m.getUserId())
        .build();
  }

  public static StudentDto toDto(Student m, Long currentGroupId) {
    if (m == null) return null;
    return StudentDto.builder()
        .id(m.getId())
        .std(m.getStd())
        .firstName(m.getFirstName())
        .lastName(m.getLastName())
        .email(m.getEmail())
        .promotionId(m.getPromotionId())
        .currentGroupId(currentGroupId)
        .build();
  }
}
