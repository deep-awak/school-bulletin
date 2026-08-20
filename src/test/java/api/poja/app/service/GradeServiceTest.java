package api.poja.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.exception.ForbiddenException;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.jpa.GradeEntity;
import api.poja.app.jpa.GradeHistoryEntity;
import api.poja.app.jpa.GroupEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.jpa.StudentGroupAssignmentEntity;
import api.poja.app.model.Role;
import api.poja.app.repository.CourseRepository;
import api.poja.app.repository.CourseTeachingRepository;
import api.poja.app.repository.GradeHistoryRepository;
import api.poja.app.repository.GradeRepository;
import api.poja.app.repository.StudentGroupAssignmentRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.security.AccessGuard;
import api.poja.app.security.CurrentUser;
import api.poja.app.validator.GradeValidator;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

  @Mock private GradeRepository gradeRepository;
  @Mock private GradeHistoryRepository gradeHistoryRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private CourseRepository courseRepository;
  @Mock private CourseTeachingRepository courseTeachingRepository;
  @Mock private StudentGroupAssignmentRepository assignmentRepository;

  private GradeService gradeService;

  private final StudentEntity student =
      StudentEntity.builder().id(1L).std("HEI-1").firstName("Jean").lastName("Rakoto").build();
  private final CourseEntity course = CourseEntity.builder().id(2L).name("Algo").code("ALG1").build();
  private final GroupEntity group = GroupEntity.builder().id(3L).name("G1").build();

  @BeforeEach
  void setUp() {
    AccessGuard accessGuard = new AccessGuard(courseTeachingRepository);
    GradeValidator gradeValidator = new GradeValidator();
    StudentGroupAssignmentService assignmentService =
        new StudentGroupAssignmentService(
            assignmentRepository, studentRepository, null, new api.poja.app.validator.AssignmentValidator());
    gradeService =
        new GradeService(
            gradeRepository,
            gradeHistoryRepository,
            studentRepository,
            courseRepository,
            gradeValidator,
            accessGuard,
            assignmentService);

    var activeAssignment =
        StudentGroupAssignmentEntity.builder()
            .id(9L)
            .student(student)
            .group(group)
            .startDate(LocalDate.of(2025, 9, 1))
            .endDate(null)
            .build();
    // lenient: not every test in this class exercises the group-lookup path (e.g. the
    // not-found and listForStudent tests return before it would be reached)
    org.mockito.Mockito.lenient()
        .when(assignmentRepository.findByStudentIdAndEndDateIsNull(1L))
        .thenReturn(Optional.of(activeAssignment));
  }

  private CurrentUser teacherAssignedToCourse() {
    when(courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(7L, 2L, 3L))
        .thenReturn(true);
    return CurrentUser.builder().userId(70L).role(Role.TEACHER).teacherId(7L).build();
  }

  @Test
  void creating_a_grade_appends_an_initial_history_row() {
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
    when(gradeRepository.save(any()))
        .thenAnswer(
            invocation -> {
              GradeEntity e = invocation.getArgument(0);
              e.setId(100L);
              return e;
            });

    var request =
        CreateGradeRequest.builder()
            .studentId(1L)
            .courseId(2L)
            .academicYear("2025-2026")
            .value(14d)
            .build();

    var grade = gradeService.create(request, teacherAssignedToCourse());

    assertThat(grade.getId()).isEqualTo(100L);
    assertThat(grade.getValue()).isEqualTo(14d);
    verify(gradeHistoryRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                (GradeHistoryEntity h) -> h.getOldValue() == null && h.getNewValue() == 14d));
  }

  @Test
  void a_teacher_not_assigned_to_the_course_cannot_create_a_grade() {
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
    when(courseTeachingRepository.existsByTeacherIdAndCourseIdAndGroupId(8L, 2L, 3L))
        .thenReturn(false);
    var otherTeacher = CurrentUser.builder().userId(80L).role(Role.TEACHER).teacherId(8L).build();

    var request =
        CreateGradeRequest.builder()
            .studentId(1L)
            .courseId(2L)
            .academicYear("2025-2026")
            .value(14d)
            .build();

    assertThatThrownBy(() -> gradeService.create(request, otherTeacher))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void updating_a_grade_preserves_the_old_value_in_history() {
    var existing =
        GradeEntity.builder()
            .id(50L)
            .student(student)
            .course(course)
            .academicYear("2025-2026")
            .value(10d)
            .authorUserId(70L)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    when(gradeRepository.findById(50L)).thenReturn(Optional.of(existing));
    when(gradeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    var request = UpdateGradeRequest.builder().value(16d).reason("Grading error").build();
    var updated = gradeService.update(50L, request, teacherAssignedToCourse());

    assertThat(updated.getValue()).isEqualTo(16d);
    verify(gradeHistoryRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                (GradeHistoryEntity h) ->
                    h.getOldValue() == 10d
                        && h.getNewValue() == 16d
                        && h.getReason().equals("Grading error")));
  }

  @Test
  void updating_a_nonexistent_grade_throws_not_found() {
    when(gradeRepository.findById(999L)).thenReturn(Optional.empty());
    var request = UpdateGradeRequest.builder().value(16d).reason("fix").build();

    assertThatThrownBy(
            () -> gradeService.update(999L, request, teacherAssignedToCourse_noStub()))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  private CurrentUser teacherAssignedToCourse_noStub() {
    return CurrentUser.builder().userId(70L).role(Role.TEACHER).teacherId(7L).build();
  }

  @Test
  void a_student_can_list_their_own_grades_but_not_someone_elses() {
    var self = CurrentUser.builder().userId(1L).role(Role.STUDENT).studentId(1L).build();
    when(gradeRepository.findByStudentId(1L)).thenReturn(java.util.List.of());

    assertThat(gradeService.listForStudent(1L, self)).isEmpty();

    var otherStudent = CurrentUser.builder().userId(2L).role(Role.STUDENT).studentId(2L).build();
    assertThatThrownBy(() -> gradeService.listForStudent(1L, otherStudent))
        .isInstanceOf(ForbiddenException.class);
  }
}
