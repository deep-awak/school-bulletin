package api.poja.app.endpoint;

import api.poja.app.dto.GradeDto;
import api.poja.app.dto.GradeHistoryDto;
import api.poja.app.dto.request.CreateGradeRequest;
import api.poja.app.dto.request.UpdateGradeRequest;
import api.poja.app.mapper.GradeHistoryMapper;
import api.poja.app.mapper.GradeMapper;
import api.poja.app.security.AuthContext;
import api.poja.app.service.GradeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@AllArgsConstructor
public class GradeEndpoint {
  private final GradeService gradeService;
  private final AuthContext authContext;

  @PostMapping
  public ResponseEntity<GradeDto> create(
      @RequestBody CreateGradeRequest request, HttpServletRequest httpRequest) {
    var author = authContext.resolve(httpRequest);
    var created = gradeService.create(request, author);
    return ResponseEntity.status(HttpStatus.CREATED).body(GradeMapper.toDto(created));
  }

  @PatchMapping("/{id}")
  public GradeDto update(
      @PathVariable Long id,
      @RequestBody UpdateGradeRequest request,
      HttpServletRequest httpRequest) {
    var author = authContext.resolve(httpRequest);
    return GradeMapper.toDto(gradeService.update(id, request, author));
  }

  @GetMapping
  public List<GradeDto> listForStudent(
      @RequestParam Long studentId, HttpServletRequest httpRequest) {
    var requester = authContext.resolve(httpRequest);
    return gradeService.listForStudent(studentId, requester).stream()
        .map(GradeMapper::toDto)
        .toList();
  }

  @GetMapping("/{id}/history")
  public List<GradeHistoryDto> history(@PathVariable Long id, HttpServletRequest httpRequest) {
    var requester = authContext.resolve(httpRequest);
    return gradeService.history(id, requester).stream().map(GradeHistoryMapper::toDto).toList();
  }
}
