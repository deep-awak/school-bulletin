package api.poja.app.dto;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class PromotionDto {
  private UUID id;
  private String name;
  private Integer startYear;
  private Integer endYear;
}