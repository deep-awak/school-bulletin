package api.poja.app.mapper;

import api.poja.app.dto.TeacherDto;
import api.poja.app.jpa.TeacherEntity;
import api.poja.app.model.Teacher;

public final class TeacherMapper {
  private TeacherMapper() {}

  public static Teacher toModel(TeacherEntity e) {
    if (e == null) return null;
    return Teacher.builder()
            .id(e.getId())
            .firstName(e.getFirstName())
            .lastName(e.getLastName())
            .email(e.getEmail())
            .userId(e.getUserId())
            .build();
  }

  public static TeacherEntity toEntity(Teacher m) {
    if (m == null) return null;
    return TeacherEntity.builder()
            .id(m.getId())
            .firstName(m.getFirstName())
            .lastName(m.getLastName())
            .email(m.getEmail())
            .userId(m.getUserId())
            .build();
  }

  public static TeacherDto toDto(Teacher m) {
    if (m == null) return null;
    return TeacherDto.builder()
            .id(m.getId())
            .firstName(m.getFirstName())
            .lastName(m.getLastName())
            .email(m.getEmail())
            .build();
  }
}