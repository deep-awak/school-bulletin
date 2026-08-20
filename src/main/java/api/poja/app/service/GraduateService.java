package api.poja.app.service;

import api.poja.app.dto.GraduateRowDto;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.GradeEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.repository.GradeRepository;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.security.AccessGuard;
import api.poja.app.security.CurrentUser;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Computes the ranking of a promotion's students (average grade, descending) for the graduate
 * export and for admin-facing promotion results, including the 3-year view.
 */
@Service
@AllArgsConstructor
public class GraduateService {
  private final PromotionRepository promotionRepository;
  private final StudentRepository studentRepository;
  private final GradeRepository gradeRepository;
  private final AccessGuard accessGuard;

  public List<GraduateRowDto> rankPromotion(Long promotionId, CurrentUser requester) {
    accessGuard.requireAdmin(requester);
    return rankPromotionUnchecked(promotionId);
  }

  /**
   * Same ranking, without the ADMIN check, for the unauthenticated Thymeleaf promotion page (see
   * README: no login flow exists for that simple consultation UI). Never expose this through the
   * REST API - only PromotionViewEndpoint should call it.
   */
  public List<GraduateRowDto> rankPromotionForView(Long promotionId) {
    return rankPromotionUnchecked(promotionId);
  }

  private List<GraduateRowDto> rankPromotionUnchecked(Long promotionId) {
    promotionRepository
        .findById(promotionId)
        .orElseThrow(() -> new ResourceNotFoundException("Promotion not found: " + promotionId));

    List<StudentEntity> students = studentRepository.findByPromotionId(promotionId);
    List<Long> studentIds = students.stream().map(StudentEntity::getId).toList();
    List<GradeEntity> grades = gradeRepository.findByStudentIdIn(studentIds);

    Map<Long, Double> averageByStudent =
        grades.stream()
            .collect(
                Collectors.groupingBy(
                    g -> g.getStudent().getId(),
                    Collectors.averagingDouble(GradeEntity::getValue)));

    return students.stream()
        .map(s -> new Object[] {s, averageByStudent.getOrDefault(s.getId(), 0d)})
        .sorted(Comparator.comparingDouble((Object[] o) -> (Double) o[1]).reversed())
        .map(
            o -> {
              StudentEntity s = (StudentEntity) o[0];
              return GraduateRowDto.builder()
                  .std(s.getStd())
                  .lastName(s.getLastName())
                  .firstName(s.getFirstName())
                  .average((Double) o[1])
                  .build();
            })
        .toList()
        .stream()
        .collect(rankingCollector());
  }

  /**
   * Admin-only view of several promotions' rankings side by side (e.g. the last 3 academic years),
   * reusing {@link #rankPromotion}.
   */
  public Map<Long, List<GraduateRowDto>> rankPromotions(
      List<Long> promotionIds, CurrentUser requester) {
    accessGuard.requireAdmin(requester);
    return promotionIds.stream()
        .collect(Collectors.toMap(id -> id, id -> rankPromotion(id, requester)));
  }

  /** Assigns Rank 1..N once the list is already sorted by descending average. */
  private java.util.stream.Collector<GraduateRowDto, ?, List<GraduateRowDto>> rankingCollector() {
    return java.util.stream.Collector.of(
        java.util.ArrayList::new,
        (list, row) -> list.add(row.toBuilder().rank(list.size() + 1).build()),
        (a, b) -> {
          a.addAll(b);
          return a;
        });
  }
}
