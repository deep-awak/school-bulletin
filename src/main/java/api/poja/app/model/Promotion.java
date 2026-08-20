package api.poja.app.model;

import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Promotion {
  private UUID id;
  private String name;
  private Integer startYear;
  private Integer endYear;
}
