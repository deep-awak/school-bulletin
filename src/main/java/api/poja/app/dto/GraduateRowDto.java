package api.poja.app.dto;

import lombok.*;

/** One row of a promotion's graduate ranking, used both by the API and the Excel export. */
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
