package api.poja.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import api.poja.app.jpa.CourseEntity;
import api.poja.app.jpa.GradeEntity;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.repository.CourseTeachingRepository;
import api.poja.app.repository.GradeRepository;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.security.AccessGuard;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The Thymeleaf promotion page has no login (see README), so its Excel download goes through
 * {@link GraduateService#rankPromotionForView(Long)} instead of the ADMIN-gated REST path. This
 * checks that shortcut produces the same ranking without needing a CurrentUser at all.
 */
@ExtendWith(MockitoExtension.class)
class GraduateServiceViewRankingTest {

  @Mock private PromotionRepository promotionRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private GradeRepository gradeRepository;
  @Mock private CourseTeachingRepository courseTeachingRepository;

  private GraduateService graduateService;

  @BeforeEach
  void setUp() {
    graduateService =
        new GraduateService(
            promotionRepository, studentRepository, gradeRepository, new AccessGuard(courseTeachingRepository));
  }

  @Test
  void rankPromotionForView_works_without_any_current_user() {
    var promotion = PromotionEntity.builder().id(1L).name("Promo 2026").build();
    var student =
        StudentEntity.builder().id(1L).std("HEI-1").firstName("Jean").lastName("Rakoto").build();
    var course = CourseEntity.builder().id(9L).name("Algo").code("ALG1").build();

    when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
    when(studentRepository.findByPromotionId(1L)).thenReturn(List.of(student));
    when(gradeRepository.findByStudentIdIn(List.of(1L)))
        .thenReturn(List.of(GradeEntity.builder().student(student).course(course).value(12d).build()));

    var ranking = graduateService.rankPromotionForView(1L);

    assertThat(ranking).hasSize(1);
    assertThat(ranking.get(0).getStd()).isEqualTo("HEI-1");
    assertThat(ranking.get(0).getRank()).isEqualTo(1);
    assertThat(ranking.get(0).getAverage()).isEqualTo(12d);
  }
}
