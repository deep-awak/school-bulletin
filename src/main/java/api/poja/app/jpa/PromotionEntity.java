package api.poja.app.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class PromotionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Integer startYear;

  @Column(nullable = false)
  private Integer endYear;
}
