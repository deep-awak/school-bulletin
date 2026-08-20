package api.poja.app.endpoint;

import api.poja.app.dto.TeacherDto;
import api.poja.app.dto.request.CreateTeacherRequest;
import api.poja.app.mapper.TeacherMapper;
import api.poja.app.service.TeacherService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@AllArgsConstructor
public class TeacherEndpoint {
  private final TeacherService teacherService;

  @PostMapping
  public ResponseEntity<TeacherDto> create(@RequestBody CreateTeacherRequest request) {
    var created = teacherService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(TeacherMapper.toDto(created));
  }

  @GetMapping("/{id}")
  public TeacherDto getById(@PathVariable Long id) {
    return TeacherMapper.toDto(teacherService.getById(id));
  }

  @GetMapping
  public List<TeacherDto> listAll() {
    return teacherService.listAll().stream().map(TeacherMapper::toDto).toList();
  }
}
