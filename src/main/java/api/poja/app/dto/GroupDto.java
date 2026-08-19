package api.poja.app.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GroupDto {
  private Long id;
  private String name;
  private Long promotionId;
}
