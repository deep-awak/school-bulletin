package api.poja.app.endpoint.event.model;

import api.poja.app.PojaGenerated;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Duration;

@PojaGenerated
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class UuidCreated extends PojaEvent {
  @JsonProperty("uuid")
  private String uuid;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(10);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
