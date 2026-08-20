package api.poja.app.service;

import api.poja.app.dto.request.SendTranscriptRequest;
import api.poja.app.endpoint.event.EventProducer;
import api.poja.app.endpoint.event.model.TranscriptEmailRequested;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.exception.ValidationException;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.mapper.GradeMapper;
import api.poja.app.model.Course;
import api.poja.app.model.Grade;
import api.poja.app.model.Promotion;
import api.poja.app.model.Student;
import api.poja.app.pdf.TranscriptPdfGenerator;
import api.poja.app.repository.CourseRepository;
import api.poja.app.repository.GradeRepository;
import api.poja.app.repository.PromotionRepository;
import api.poja.app.repository.StudentRepository;
import api.poja.app.security.AccessGuard;
import api.poja.app.security.CurrentUser;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Orchestrates transcript generation end to end: PDF -> S3 -> async email, per the flow imposed by
 * the project brief. The HTTP response only waits for PDF generation and upload; the email itself
 * is delegated to the existing POJA async mechanism.
 */
@Service
@AllArgsConstructor
public class TranscriptService {
  private final StudentRepository studentRepository;
  private final PromotionRepository promotionRepository;
  private final GradeRepository gradeRepository;
  private final CourseRepository courseRepository;
  private final TranscriptPdfGenerator pdfGenerator;
  private final StorageService storageService;
  private final AccessGuard accessGuard;
  private final EventProducer<TranscriptEmailRequested> eventProducer;

  public String generateAndUpload(Long studentId, String academicYear, CurrentUser requester) {
    accessGuard.requireSelfOrStaff(requester, studentId);

    var studentEntity =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
    Student student = api.poja.app.mapper.StudentMapper.toModel(studentEntity);

    Promotion promotion =
        api.poja.app.mapper.PromotionMapper.toModel(
            promotionRepository
                .findById(student.getPromotionId())
                .orElseThrow(
                    () ->
                        new ResourceNotFoundException(
                            "Promotion not found: " + student.getPromotionId())));

    List<Grade> grades =
        gradeRepository.findByStudentIdAndAcademicYear(studentId, academicYear).stream()
            .map(GradeMapper::toModel)
            .toList();

    Map<Long, Course> coursesById =
        courseRepository
            .findAllById(grades.stream().map(Grade::getCourseId).distinct().toList())
            .stream()
            .collect(
                Collectors.toMap(CourseEntity::getId, api.poja.app.mapper.CourseMapper::toModel));

    File pdf;
    try {
      pdf = pdfGenerator.generate(student, promotion, academicYear, grades, coursesById);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    String bucketKey = "transcripts/" + student.getStd() + "/" + academicYear + "/transcript.pdf";
    String url = storageService.upload(pdf, bucketKey);

    return url;
  }

  public void sendByEmail(SendTranscriptRequest request, CurrentUser requester) {
    if (request == null || request.getStudentId() == null) {
      throw new ValidationException("Student id is required");
    }
    if (request.getAcademicYear() == null || request.getAcademicYear().isBlank()) {
      throw new ValidationException("Academic year is required");
    }

    var studentEntity =
        studentRepository
            .findById(request.getStudentId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Student not found: " + request.getStudentId()));
    Student student = api.poja.app.mapper.StudentMapper.toModel(studentEntity);

    String url = generateAndUpload(request.getStudentId(), request.getAcademicYear(), requester);

    eventProducer.accept(
        List.of(
            TranscriptEmailRequested.builder()
                .to(student.getEmail())
                .studentFullName(student.getFirstName() + " " + student.getLastName())
                .academicYear(request.getAcademicYear())
                .transcriptUrl(url)
                .build()));
  }
}
