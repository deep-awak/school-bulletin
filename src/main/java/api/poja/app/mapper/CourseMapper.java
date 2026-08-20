package api.poja.app.mapper;

import api.poja.app.dto.CourseDto;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.model.Course;

public final class CourseMapper {
  private CourseMapper() {}

  public static Course toModel(CourseEntity e) {
    if (e == null) return null;
    return Course.builder().id(e.getId()).name(e.getName()).code(e.getCode()).build();
  }

  public static CourseEntity toEntity(Course m) {
    if (m == null) return null;
    return CourseEntity.builder().id(m.getId()).name(m.getName()).code(m.getCode()).build();
  }

  public static CourseDto toDto(Course m) {
    if (m == null) return null;
    return CourseDto.builder().id(m.getId()).name(m.getName()).code(m.getCode()).build();
  }
}
