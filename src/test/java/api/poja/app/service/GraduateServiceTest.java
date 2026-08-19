package api.poja.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import api.poja.app.exception.ForbiddenException;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.jpa.GradeEntity;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.model.Role;
import api.poja.app.repository.CourseTeachingRepository;
import api.poja.app.repository.GradeRepository;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.security.AccessGuard;
import api.poja.app.security.CurrentUser;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GraduateServiceTest {

  @Mock private PromotionRepository promotionRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private GradeRepository gradeRepository;
  @Mock private CourseTeachingRepository courseTeachingRepository;

  private GraduateService graduateService;

  private final PromotionEntity promotion = PromotionEntity.builder().id(1L).name("Promo 2026").build();
  private final StudentEntity s1 =
      StudentEntity.builder().id(1L).std("HEI-1").firstName("Jean").lastName("Rakoto").build();
  private final StudentEntity s2 =
      StudentEntity.builder().id(2L).std("HEI-2").firstName("Marie").lastName("Rasoa").build();
  private final CourseEntity course = CourseEntity.builder().id(9L).name("Algo").code("ALG1").build();

  @BeforeEach
  void setUp() {
    graduateService =
        new GraduateService(
            promotionRepository, studentRepository, gradeRepository, new AccessGuard(courseTeachingRepository));
  }

  private CurrentUser admin() {
    return CurrentUser.builder().userId(1L).role(Role.ADMIN).build();
  }

  @Test
  void ranks_students_by_descending_average_and_assigns_rank_one_upwards() {
    when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
    when(studentRepository.findByPromotionId(1L)).thenReturn(List.of(s1, s2));
    when(gradeRepository.findByStudentIdIn(List.of(1L, 2L)))
        .thenReturn(
            List.of(
                GradeEntity.builder().student(s1).course(course).value(10d).build(),
                GradeEntity.builder().student(s2).course(course).value(18d).build()));

    var ranking = graduateService.rankPromotion(1L, admin());

    assertThat(ranking).hasSize(2);
    assertThat(ranking.get(0).getStd()).isEqualTo("HEI-2");
    assertThat(ranking.get(0).getRank()).isEqualTo(1);
    assertThat(ranking.get(1).getStd()).isEqualTo("HEI-1");
    assertThat(ranking.get(1).getRank()).isEqualTo(2);
  }

  @Test
  void a_non_admin_cannot_view_promotion_ranking() {
    var teacher = CurrentUser.builder().userId(7L).role(Role.TEACHER).teacherId(7L).build();

    assertThatThrownBy(() -> graduateService.rankPromotion(1L, teacher))
        .isInstanceOf(ForbiddenException.class);
  }
}
