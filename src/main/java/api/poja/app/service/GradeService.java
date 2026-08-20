package api.poja.app.service;

import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.jpa.GradeEntity;
import api.poja.app.jpa.GradeHistoryEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.mapper.GradeHistoryMapper;
import api.poja.app.mapper.GradeMapper;
import api.poja.app.model.Grade;
import api.poja.app.model.GradeHistory;
import api.poja.app.repository.CourseRepository;
import api.poja.app.repository.GradeHistoryRepository;
import api.poja.app.repository.GradeRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.security.AccessGuard;
import api.poja.app.security.CurrentUser;
import api.poja.app.validator.GradeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeService {

  private final GradeRepository gradeRepository;
  private final GradeHistoryRepository gradeHistoryRepository;
  private final StudentRepository studentRepository;
  private final CourseRepository courseRepository;
  private final GradeValidator gradeValidator;
  private final AccessGuard accessGuard;
  private final StudentGroupAssignmentService studentGroupAssignmentService;

  @Transactional
  public Grade create(CreateGradeRequest request, CurrentUser author) {
    gradeValidator.validate(request);

    StudentEntity student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.getStudentId()));

    CourseEntity course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));

    String studentGroupId = studentGroupAssignmentService.currentGroupId(student.getId());
    accessGuard.requireCourseTeacherOrAdmin(author, course.getId(), studentGroupId);

    Instant now = Instant.now();

    GradeEntity entity = GradeEntity.builder()
            .student(student)
            .course(course)
            .academicYear(request.getAcademicYear())
            .value(request.getValue())
            .authorUserId(author.getUserId())
            .createdAt(now)
            .updatedAt(now)
            .build();

    GradeEntity saved = gradeRepository.save(entity);

    gradeHistoryRepository.save(
            GradeHistoryEntity.builder()
                    .grade(saved)
                    .oldValue(null)
                    .newValue(saved.getValue())
                    .reason("Initial grade entry")
                    .authorUserId(author.getUserId())
                    .changedAt(now)
                    .build()
    );

    return GradeMapper.toModel(saved);
  }

  @Transactional
  public Grade update(UUID gradeId, UpdateGradeRequest request, CurrentUser author) {
    gradeValidator.validate(request);

    GradeEntity grade = gradeRepository.findById(gradeId)
            .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));

    String studentGroupId = studentGroupAssignmentService.currentGroupId(grade.getStudent().getId());
    accessGuard.requireCourseTeacherOrAdmin(author, grade.getCourse().getId(), studentGroupId);

    double oldValue = grade.getValue();
    Instant now = Instant.now();

    grade.setValue(request.getValue());
    grade.setUpdatedAt(now);
    GradeEntity saved = gradeRepository.save(grade);

    gradeHistoryRepository.save(
            GradeHistoryEntity.builder()
                    .grade(saved)
                    .oldValue(oldValue)
                    .newValue(request.getValue())
                    .reason(request.getReason())
                    .authorUserId(author.getUserId())
                    .changedAt(now)
                    .build()
    );

    return GradeMapper.toModel(saved);
  }

  public List<Grade> listForStudent(String studentId, CurrentUser requester) {
    accessGuard.requireSelfOrStaff(requester, studentId);
    return gradeRepository.findByStudentId(studentId).stream()
            .map(GradeMapper::toModel)
            .toList();
  }

  public List<GradeHistory> history(UUID gradeId, CurrentUser requester) {
    GradeEntity grade = gradeRepository.findById(gradeId)
            .orElseThrow(() -> new ResourceNotFoundException("Grade not found: " + gradeId));

    accessGuard.requireSelfOrStaff(requester, grade.getStudent().getId());

    return gradeHistoryRepository.findByGradeIdOrderByChangedAtDesc(gradeId).stream()
            .map(GradeHistoryMapper::toModel)
            .toList();
  }
}