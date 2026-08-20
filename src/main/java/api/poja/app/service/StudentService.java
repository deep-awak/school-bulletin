package api.poja.app.service;

import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.dto.request.CreateStudentRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.PromotionEntity;
import api.poja.app.jpa.StudentEntity;
import api.poja.app.mapper.StudentMapper;
import api.poja.app.model.Student;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.util.IdGenerator;
import api.poja.app.validator.StudentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {
  private final StudentRepository studentRepository;
  private final PromotionRepository promotionRepository;
  private final StudentValidator studentValidator;
  private final StudentGroupAssignmentService studentGroupAssignmentService;
  private final IdGenerator idGenerator;

  @Transactional
  public Student create(CreateStudentRequest request) {
    studentValidator.validate(request);

    PromotionEntity promotion = promotionRepository.findById(request.getPromotionId())
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found: " + request.getPromotionId()));

    String generatedId = idGenerator.generateStudentId();

    StudentEntity entity = StudentEntity.builder()
            .id(generatedId)
            .std(request.getStd())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .promotion(promotion)
            .build();

    StudentEntity saved = studentRepository.save(entity);

    studentGroupAssignmentService.assign(
            AssignStudentGroupRequest.builder()
                    .studentId(saved.getId())
                    .groupId(request.getGroupId())
                    .startDate(LocalDate.now())
                    .build());

    return StudentMapper.toModel(saved);
  }

  public Student getById(String id) {
    return StudentMapper.toModel(
            studentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id)));
  }

  public List<Student> listByPromotion(UUID promotionId) {
    return studentRepository.findByPromotionId(promotionId).stream()
            .map(StudentMapper::toModel)
            .toList();
  }

  public String currentGroupId(String studentId) {
    return studentGroupAssignmentService.currentGroupId(studentId);
  }
}