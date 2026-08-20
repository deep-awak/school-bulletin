package api.poja.app.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "student", uniqueConstraints = @UniqueConstraint(columnNames = "std"))
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class StudentEntity {
  @Id
  private String id;

  @Column(nullable = false, unique = true)
  private String std;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "promotion_id", nullable = false)
  private PromotionEntity promotion;

  @Column(name = "user_id")
  private UUID userId;
}