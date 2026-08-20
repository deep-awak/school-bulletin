package api.poja.app.endpoint.event.model;

import lombok.*;

import java.time.Duration;

/**
 * Fired once a student's transcript PDF has been generated and uploaded to S3. Consumed
 * asynchronously by the existing POJA email worker mechanism to actually send the email, so the
 * HTTP request that triggered it doesn't have to wait on SES.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class TranscriptEmailRequested extends PojaEvent {
  private String to;
  private String studentFullName;
  private String academicYear;
  private String transcriptUrl;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(45);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
