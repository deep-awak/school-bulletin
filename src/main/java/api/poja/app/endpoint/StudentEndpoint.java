package api.poja.app.endpoint;

import api.poja.app.dto.StudentDto;
import api.poja.app.dto.request.AssignStudentGroupRequest;
import api.poja.app.dto.request.CreateStudentRequest;
import api.poja.app.mapper.StudentMapper;
import api.poja.app.model.StudentGroupAssignment;
import api.poja.app.service.StudentGroupAssignmentService;
import api.poja.app.service.StudentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentEndpoint {

  private final StudentService studentService;
  private final StudentGroupAssignmentService studentGroupAssignmentService;

  @PostMapping
  public ResponseEntity<StudentDto> create(@RequestBody CreateStudentRequest request) {
    var created = studentService.create(request);
    String groupId = studentService.currentGroupId(created.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(StudentMapper.toDto(created, groupId));
  }

  @GetMapping("/{id}")
  public StudentDto getById(@PathVariable String id) {
    var student = studentService.getById(id);
    return StudentMapper.toDto(student, studentService.currentGroupId(id));
  }

  @GetMapping
  public List<StudentDto> listByPromotion(@RequestParam UUID promotionId) {
    return studentService.listByPromotion(promotionId).stream()
        .map(s -> StudentMapper.toDto(s, studentService.currentGroupId(s.getId())))
        .toList();
  }

  @PostMapping("/group-assignments")
  public ResponseEntity<Void> reassignGroup(@RequestBody AssignStudentGroupRequest request) {
    studentGroupAssignmentService.assign(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{id}/group-history")
  public List<StudentGroupAssignment> groupHistory(@PathVariable String id) {
    return studentGroupAssignmentService.history(id);
  }
}
