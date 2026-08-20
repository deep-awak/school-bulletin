package api.poja.app.service;

import api.poja.app.dto.request.CreateCourseRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.CourseEntity;
import api.poja.app.mapper.CourseMapper;
import api.poja.app.model.Course;
import api.poja.app.repository.CourseRepository;
import api.poja.app.validator.CourseValidator;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseService {
  private final CourseRepository courseRepository;
  private final CourseValidator courseValidator;

  public Course create(CreateCourseRequest request) {
    courseValidator.validate(request);
    CourseEntity entity =
        CourseEntity.builder().name(request.getName()).code(request.getCode()).build();
    return CourseMapper.toModel(courseRepository.save(entity));
  }

  public Course getById(UUID id) {
    return CourseMapper.toModel(
        courseRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id)));
  }

  public List<Course> listAll() {
    return courseRepository.findAll().stream().map(CourseMapper::toModel).toList();
  }
}
