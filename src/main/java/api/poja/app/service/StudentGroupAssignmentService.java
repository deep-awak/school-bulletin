package api.poja.app.service;

import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.GroupEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.jpa.StudentGroupAssignmentEntity;
import api.poja.app.model.StudentGroupAssignment;
import api.poja.app.repository.GroupRepository;
import api.poja.app.repository.StudentGroupAssignmentRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.validator.AssignmentValidator;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles moving a student from one group to another while preserving full history: the
 * previously active assignment is closed (endDate set) and a new one is opened, instead of
 * mutating a plain Student -> Group reference.
 */
@Service
@AllArgsConstructor
public class StudentGroupAssignmentService {
  private final StudentGroupAssignmentRepository assignmentRepository;
  private final StudentRepository studentRepository;
  private final GroupRepository groupRepository;
  private final AssignmentValidator assignmentValidator;

  @Transactional
  public StudentGroupAssignment assign(AssignStudentGroupRequest request) {
    assignmentValidator.validate(request);

    StudentEntity student =
        studentRepository
            .findById(request.getStudentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Student not found: " + request.getStudentId()));
    GroupEntity group =
        groupRepository
            .findById(request.getGroupId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Group not found: " + request.getGroupId()));

    Optional<StudentGroupAssignmentEntity> current =
        assignmentRepository.findByStudentIdAndEndDateIsNull(student.getId());
    current.ifPresent(
        active -> {
          active.setEndDate(request.getStartDate().minusDays(1).isBefore(active.getStartDate())
              ? active.getStartDate()
              : request.getStartDate().minusDays(1));
          assignmentRepository.save(active);
        });

    StudentGroupAssignmentEntity newAssignment =
        StudentGroupAssignmentEntity.builder()
            .student(student)
            .group(group)
            .startDate(request.getStartDate())
            .endDate(null)
            .build();

    return toModel(assignmentRepository.save(newAssignment));
  }

  public Long currentGroupId(Long studentId) {
    return assignmentRepository
        .findByStudentIdAndEndDateIsNull(studentId)
        .map(a -> a.getGroup().getId())
        .orElse(null);
  }

  public List<StudentGroupAssignment> history(Long studentId) {
    return assignmentRepository.findByStudentIdOrderByStartDateDesc(studentId).stream()
        .map(StudentGroupAssignmentService::toModel)
        .toList();
  }

  private static StudentGroupAssignment toModel(StudentGroupAssignmentEntity e) {
    return StudentGroupAssignment.builder()
        .id(e.getId())
        .studentId(e.getStudent().getId())
        .groupId(e.getGroup().getId())
        .startDate(e.getStartDate())
        .endDate(e.getEndDate())
        .build();
  }
}
