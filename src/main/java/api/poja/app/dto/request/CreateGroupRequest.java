package api.poja.app.dto.request;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateGroupRequest {
  private String name;
  private UUID promotionId;
}
