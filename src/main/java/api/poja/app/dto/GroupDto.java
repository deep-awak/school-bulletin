package api.poja.app.dto;

import lombok.*;

import java.util.UUID;

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