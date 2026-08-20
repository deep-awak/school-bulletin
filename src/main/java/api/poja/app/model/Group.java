package api.poja.app.model;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Group {
  private String id;
  private String name;
  private UUID promotionId;
}
