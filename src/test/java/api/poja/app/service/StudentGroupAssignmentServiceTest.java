package api.poja.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.jpa.GroupEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.jpa.StudentGroupAssignmentEntity;
import api.poja.app.repository.GroupRepository;
import api.poja.app.repository.StudentGroupAssignmentRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.validator.AssignmentValidator;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentGroupAssignmentServiceTest {

  @Mock private StudentGroupAssignmentRepository assignmentRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private GroupRepository groupRepository;

  private StudentGroupAssignmentService service;

  private final StudentEntity student = StudentEntity.builder().id(1L).std("HEI-1").build();
  private final GroupEntity oldGroup = GroupEntity.builder().id(10L).name("G1").build();
  private final GroupEntity newGroup = GroupEntity.builder().id(20L).name("G2").build();

  @BeforeEach
  void setUp() {
    service =
        new StudentGroupAssignmentService(
            assignmentRepository, studentRepository, groupRepository, new AssignmentValidator());
  }

  @Test
  void reassigning_a_student_closes_the_previous_assignment_and_opens_a_new_one() {
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(groupRepository.findById(20L)).thenReturn(Optional.of(newGroup));

    var activeAssignment =
        StudentGroupAssignmentEntity.builder()
            .id(5L)
            .student(student)
            .group(oldGroup)
            .startDate(LocalDate.of(2024, 9, 1))
            .endDate(null)
            .build();
    when(assignmentRepository.findByStudentIdAndEndDateIsNull(1L))
        .thenReturn(Optional.of(activeAssignment));
    when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var request =
        AssignStudentGroupRequest.builder()
            .studentId(1L)
            .groupId(20L)
            .startDate(LocalDate.of(2025, 9, 1))
            .build();

    var result = service.assign(request);

    // The old assignment must be persisted with an endDate rather than deleted.
    ArgumentCaptor<StudentGroupAssignmentEntity> captor =
        ArgumentCaptor.forClass(StudentGroupAssignmentEntity.class);
    verify(assignmentRepository, org.mockito.Mockito.times(2)).save(captor.capture());

    var savedOld =
        captor.getAllValues().stream().filter(a -> a.getId() != null && a.getId().equals(5L)).findFirst();
    assertThat(savedOld).isPresent();
    assertThat(savedOld.get().getEndDate()).isNotNull();

    assertThat(result.getGroupId()).isEqualTo(20L);
    assertThat(result.getEndDate()).isNull();
  }

  @Test
  void assigning_a_student_with_no_prior_group_just_opens_the_first_assignment() {
    when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    when(groupRepository.findById(20L)).thenReturn(Optional.of(newGroup));
    when(assignmentRepository.findByStudentIdAndEndDateIsNull(1L)).thenReturn(Optional.empty());
    when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var request =
        AssignStudentGroupRequest.builder()
            .studentId(1L)
            .groupId(20L)
            .startDate(LocalDate.of(2025, 9, 1))
            .build();

    var result = service.assign(request);

    verify(assignmentRepository, org.mockito.Mockito.times(1)).save(any());
    assertThat(result.getGroupId()).isEqualTo(20L);
  }
}
