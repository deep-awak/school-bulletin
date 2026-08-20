package api.poja.app.mapper;

import api.poja.app.dto.GradeHistoryDto;
import api.poja.app.jpa.GradeHistoryEntity;
import api.poja.app.model.GradeHistory;

public final class GradeHistoryMapper {
  private GradeHistoryMapper() {}

  public static GradeHistory toModel(GradeHistoryEntity e) {
    if (e == null) return null;
    return GradeHistory.builder()
        .id(e.getId())
        .gradeId(e.getGrade() != null ? e.getGrade().getId() : null)
        .oldValue(e.getOldValue())
        .newValue(e.getNewValue())
        .reason(e.getReason())
        .authorUserId(e.getAuthorUserId())
        .changedAt(e.getChangedAt())
        .build();
  }

  public static GradeHistoryDto toDto(GradeHistory m) {
    if (m == null) return null;
    return GradeHistoryDto.builder()
        .id(m.getId())
        .gradeId(m.getGradeId())
        .oldValue(m.getOldValue())
        .newValue(m.getNewValue())
        .reason(m.getReason())
        .authorUserId(m.getAuthorUserId())
        .changedAt(m.getChangedAt())
        .build();
  }
}
