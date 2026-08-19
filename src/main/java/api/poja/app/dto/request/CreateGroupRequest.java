package api.poja.app.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreateGroupRequest {
  private String name;
  private Long promotionId;
}
