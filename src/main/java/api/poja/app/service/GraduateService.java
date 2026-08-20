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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraduateService {

  private final PromotionRepository promotionRepository;
  private final StudentRepository studentRepository;
  private final GradeRepository gradeRepository;
  private final AccessGuard accessGuard;

  public List<GraduateRowDto> rankPromotion(UUID promotionId, CurrentUser requester) {
    accessGuard.requireAdmin(requester);
    return rankPromotionUnchecked(promotionId);
  }

  public List<GraduateRowDto> rankPromotionForView(UUID promotionId) {
    return rankPromotionUnchecked(promotionId);
  }

  private List<GraduateRowDto> rankPromotionUnchecked(UUID promotionId) {
    promotionRepository.findById(promotionId)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found: " + promotionId));

    List<StudentEntity> students = studentRepository.findByPromotionId(promotionId);
    List<String> studentIds = students.stream().map(StudentEntity::getId).toList();

    List<GradeEntity> grades = gradeRepository.findByStudentIdIn(studentIds);

    Map<String, Double> averageByStudent = grades.stream()
            .collect(Collectors.groupingBy(
                    g -> g.getStudent().getId(),
                    Collectors.averagingDouble(GradeEntity::getValue)
            ));

    return students.stream()
            .map(s -> new Object[]{s, averageByStudent.getOrDefault(s.getId(), 0d)})
            .sorted(Comparator.comparingDouble((Object[] o) -> (Double) o[1]).reversed())
            .map(o -> {
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

  public Map<UUID, List<GraduateRowDto>> rankPromotions(List<UUID> promotionIds, CurrentUser requester) {
    accessGuard.requireAdmin(requester);
    return promotionIds.stream()
            .collect(Collectors.toMap(id -> id, id -> rankPromotion(id, requester)));
  }

  private java.util.stream.Collector<GraduateRowDto, ?, List<GraduateRowDto>> rankingCollector() {
    return java.util.stream.Collector.of(
            java.util.ArrayList::new,
            (list, row) -> list.add(row.toBuilder().rank(list.size() + 1).build()),
            (a, b) -> {
              a.addAll(b);
              return a;
            }
    );
  }
}