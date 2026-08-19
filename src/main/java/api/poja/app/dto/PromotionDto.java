package api.poja.app.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class PromotionDto {
  private Long id;
  private String name;
  private Integer startYear;
  private Integer endYear;
}
