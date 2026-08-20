package api.poja.app.dto;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GroupDto {
  private String id;
  private String name;
  private UUID promotionId;
}
