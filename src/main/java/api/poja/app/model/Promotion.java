package api.poja.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Promotion {
  private Long id;
  private String name;
  private Integer startYear;
  private Integer endYear;
}
