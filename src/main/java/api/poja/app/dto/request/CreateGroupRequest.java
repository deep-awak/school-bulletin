package api.poja.app.dto.request;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateGroupRequest {
  private String name;
  private UUID promotionId;
}