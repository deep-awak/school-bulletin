package api.poja.app.endpoint;

import api.poja.app.dto.CourseDto;
import api.poja.app.dto.request.AssignCourseTeachingRequest;
import api.poja.app.dto.request.CreateCourseRequest;
import api.poja.app.mapper.CourseMapper;
import api.poja.app.service.CourseService;
import api.poja.app.service.CourseTeachingService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@AllArgsConstructor
public class CourseEndpoint {
  private final CourseService courseService;
  private final CourseTeachingService courseTeachingService;

  @PostMapping
  public ResponseEntity<CourseDto> create(@RequestBody CreateCourseRequest request) {
    var created = courseService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(CourseMapper.toDto(created));
  }

  @GetMapping("/{id}")
  public CourseDto getById(@PathVariable Long id) {
    return CourseMapper.toDto(courseService.getById(id));
  }

  @GetMapping
  public List<CourseDto> listAll() {
    return courseService.listAll().stream().map(CourseMapper::toDto).toList();
  }

  @PostMapping("/teaching-assignments")
  public ResponseEntity<Void> assignTeaching(@RequestBody AssignCourseTeachingRequest request) {
    courseTeachingService.assign(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
