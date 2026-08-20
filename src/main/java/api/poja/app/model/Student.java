package api.poja.app.model;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Student {
  private String id;
  private String std;
  private String firstName;
  private String lastName;
  private String email;
  private UUID promotionId;
  private UUID userId;
}