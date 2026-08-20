package api.poja.app.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CreatePromotionRequest {
  private String name;
  private Integer startYear;
  private Integer endYear;
}
