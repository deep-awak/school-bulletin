package api.poja.app.service;

import api.poja.app.dto.request.AssignCourseTeachingRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.jpa.CourseTeachingEntity;
import api.poja.app.jpa.GroupEntity;
import api.poja.app.jpa.TeacherEntity;
import api.poja.app.model.CourseTeaching;
import api.poja.app.repository.CourseRepository;
import api.poja.app.repository.CourseTeachingRepository;
import api.poja.app.repository.GroupRepository;
import api.poja.app.repository.TeacherRepository;
import api.poja.app.validator.AssignmentValidator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseTeachingService {
  private final CourseTeachingRepository courseTeachingRepository;
  private final CourseRepository courseRepository;
  private final TeacherRepository teacherRepository;
  private final GroupRepository groupRepository;
  private final AssignmentValidator assignmentValidator;

  public CourseTeaching assign(AssignCourseTeachingRequest request) {
    assignmentValidator.validate(request);

    CourseEntity course =
        courseRepository
            .findById(request.getCourseId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));
    TeacherEntity teacher =
        teacherRepository
            .findById(request.getTeacherId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Teacher not found: " + request.getTeacherId()));
    GroupEntity group =
        groupRepository
            .findById(request.getGroupId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Group not found: " + request.getGroupId()));

    CourseTeachingEntity entity =
        CourseTeachingEntity.builder()
            .course(course)
            .teacher(teacher)
            .group(group)
            .academicYear(request.getAcademicYear())
            .build();

    CourseTeachingEntity saved = courseTeachingRepository.save(entity);
    return toModel(saved);
  }

  public List<CourseTeaching> listByTeacher(String teacherId) {
    return courseTeachingRepository.findByTeacherId(teacherId).stream()
        .map(CourseTeachingService::toModel)
        .toList();
  }

  private static CourseTeaching toModel(CourseTeachingEntity e) {
    return CourseTeaching.builder()
        .id(e.getId())
        .courseId(e.getCourse().getId())
        .teacherId(e.getTeacher().getId())
        .groupId(e.getGroup().getId())
        .academicYear(e.getAcademicYear())
        .build();
  }
}
