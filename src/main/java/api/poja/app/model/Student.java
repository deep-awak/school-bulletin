package api.poja.app.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Student {
  private Long id;
  private String std;
  private String firstName;
  private String lastName;
  private String email;
  private Long promotionId;
  private Long userId;
}
