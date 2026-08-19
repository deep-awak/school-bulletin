package api.poja.app.mapper;

import api.poja.app.dto.GradeDto;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.jpa.GradeEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.model.Grade;

public final class GradeMapper {
  private GradeMapper() {}

  public static Grade toModel(GradeEntity e) {
    if (e == null) return null;
    return Grade.builder()
        .id(e.getId())
        .studentId(e.getStudent() != null ? e.getStudent().getId() : null)
        .courseId(e.getCourse() != null ? e.getCourse().getId() : null)
        .academicYear(e.getAcademicYear())
        .value(e.getValue())
        .authorUserId(e.getAuthorUserId())
        .createdAt(e.getCreatedAt())
        .updatedAt(e.getUpdatedAt())
        .build();
  }

  public static GradeEntity toEntity(Grade m, StudentEntity student, CourseEntity course) {
    if (m == null) return null;
    return GradeEntity.builder()
        .id(m.getId())
        .student(student)
        .course(course)
        .academicYear(m.getAcademicYear())
        .value(m.getValue())
        .authorUserId(m.getAuthorUserId())
        .createdAt(m.getCreatedAt())
        .updatedAt(m.getUpdatedAt())
        .build();
  }

  public static GradeDto toDto(Grade m) {
    if (m == null) return null;
    return GradeDto.builder()
        .id(m.getId())
        .studentId(m.getStudentId())
        .courseId(m.getCourseId())
        .academicYear(m.getAcademicYear())
        .value(m.getValue())
        .authorUserId(m.getAuthorUserId())
        .createdAt(m.getCreatedAt())
        .updatedAt(m.getUpdatedAt())
        .build();
  }
}
