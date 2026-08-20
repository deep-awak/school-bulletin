package api.poja.app.endpoint;

import api.poja.app.dto.request.SendTranscriptRequest;
import api.poja.app.security.AuthContext;
import api.poja.app.service.TranscriptService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transcripts")
@AllArgsConstructor
public class TranscriptEndpoint {
  private final TranscriptService transcriptService;
  private final AuthContext authContext;

  /** Generates the PDF, uploads it to S3 and returns a presigned link (synchronous). */
  @PostMapping("/generate")
  public Map<String, String> generate(
      @RequestParam Long studentId,
      @RequestParam String academicYear,
      HttpServletRequest httpRequest) {
    var requester = authContext.resolve(httpRequest);
    String url = transcriptService.generateAndUpload(studentId, academicYear, requester);
    return Map.of("url", url);
  }

  /** Generates the PDF, uploads it, then dispatches the async POJA email event. */
  @PostMapping("/send")
  public ResponseEntity<Void> send(
      @RequestBody SendTranscriptRequest request, HttpServletRequest httpRequest) {
    var requester = authContext.resolve(httpRequest);
    transcriptService.sendByEmail(request, requester);
    return ResponseEntity.accepted().build();
  }
}
