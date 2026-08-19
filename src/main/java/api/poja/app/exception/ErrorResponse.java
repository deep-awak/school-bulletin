package api.poja.app.exception;

import java.time.Instant;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ErrorResponse {
  private Instant timestamp;
  private int status;
  private String error;
  private String message;
}
