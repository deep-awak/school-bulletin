package api.poja.app.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GraduateRowDto {
  private Integer rank;
  private String std;
  private String lastName;
  private String firstName;
  private Double average;
}
