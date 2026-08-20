package api.poja.app.endpoint;

import api.poja.app.dto.request.SendTranscriptRequest;
import api.poja.app.security.AuthContext;
import api.poja.app.service.TranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transcripts")
@RequiredArgsConstructor
public class TranscriptEndpoint {

  private final TranscriptService transcriptService;
  private final AuthContext authContext;

  @PostMapping("/generate")
  public Map<String, String> generate(
          @RequestParam String studentId,
          @RequestParam String academicYear) {
    var requester = authContext.getCurrentUser();
    String url = transcriptService.generateAndUpload(studentId, academicYear, requester);
    return Map.of("url", url);
  }

  @PostMapping("/send")
  public ResponseEntity<Void> send(@RequestBody SendTranscriptRequest request) {
    var requester = authContext.getCurrentUser();
    transcriptService.sendByEmail(request, requester);
    return ResponseEntity.accepted().build();
  }
}