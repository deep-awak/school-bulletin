package api.poja.app.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class SendTranscriptRequest {
  private Long studentId;
  private String academicYear;
}
