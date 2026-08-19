package api.poja.app.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CourseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String code;
}
