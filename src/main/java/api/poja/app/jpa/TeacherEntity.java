package api.poja.app.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class TeacherEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @Column(name = "user_id")
  private Long userId;
}
