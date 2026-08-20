package api.poja.app.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_group")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GroupEntity {
  @Id private String id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "promotion_id", nullable = false)
  private PromotionEntity promotion;
}
