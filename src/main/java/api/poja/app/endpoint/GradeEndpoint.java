package api.poja.app.endpoint;

import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.GradeDto;
import api.poja.app.dto.GradeHistoryDto;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.mapper.GradeHistoryMapper;
import api.poja.app.mapper.GradeMapper;
import api.poja.app.security.AuthContext;
import api.poja.app.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeEndpoint {

  private final GradeService gradeService;
  private final AuthContext authContext;

  @PostMapping
  public ResponseEntity<GradeDto> create(@RequestBody CreateGradeRequest request) {
    var author = authContext.getCurrentUser();
    var created = gradeService.create(request, author);
    return ResponseEntity.status(HttpStatus.CREATED).body(GradeMapper.toDto(created));
  }

  @PatchMapping("/{id}")
  public GradeDto update(@PathVariable UUID id, @RequestBody UpdateGradeRequest request) {
    var author = authContext.getCurrentUser();
    return GradeMapper.toDto(gradeService.update(id, request, author));
  }

  @GetMapping
  public List<GradeDto> listForStudent(@RequestParam String studentId) {
    var requester = authContext.getCurrentUser();
    return gradeService.listForStudent(studentId, requester).stream()
            .map(GradeMapper::toDto)
            .toList();
  }

  @GetMapping("/{id}/history")
  public List<GradeHistoryDto> history(@PathVariable UUID id) {
    var requester = authContext.getCurrentUser();
    return gradeService.history(id, requester).stream()
            .map(GradeHistoryMapper::toDto)
            .toList();
  }
}