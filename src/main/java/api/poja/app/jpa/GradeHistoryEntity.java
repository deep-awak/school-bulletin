package api.poja.app.jpa;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(name = "grade_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GradeHistoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grade_id", nullable = false)
  private GradeEntity grade;

  private Double oldValue;

  @Column(nullable = false)
  private Double newValue;

  @Column(nullable = false)
  private String reason;

  @Column(name = "author_user_id", nullable = false)
  private Long authorUserId;

  @Column(nullable = false)
  private Instant changedAt;
}
