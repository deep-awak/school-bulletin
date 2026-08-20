package api.poja.app.service;

import api.poja.app.jpa.BootstrapConfigEntity;
import api.poja.app.jpa.RoleEntity;
import api.poja.app.jpa.UserEntity;
import api.poja.app.model.Role;
import api.poja.app.repository.BootstrapConfigRepository;
import api.poja.app.repository.RoleRepository;
import api.poja.app.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminBootstrapService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final BootstrapConfigRepository bootstrapConfigRepository;
  private final PasswordEncoder passwordEncoder;
  private final JdbcTemplate jdbcTemplate;

  @Value("${app.admin.email:admin@hei.mg}")
  private String adminEmail;

  @Value("${app.admin.password:Admin123!}")
  private String adminPassword;

  @Value("${app.admin.full-name:Administrateur}")
  private String adminFullName;

  private static final String BOOTSTRAP_KEY = "admin_bootstrapped";

  @PostConstruct
  public void initBootstrapConfigTable() {
    try {
      String createTableSql =
          "CREATE TABLE IF NOT EXISTS bootstrap_config ("
              + "id BIGSERIAL PRIMARY KEY, "
              + "key VARCHAR(100) NOT NULL UNIQUE, "
              + "value VARCHAR(255) NOT NULL, "
              + "created_at TIMESTAMP NOT NULL DEFAULT now())";
      jdbcTemplate.execute(createTableSql);
      log.info("Bootstrap config table initialized successfully.");
    } catch (Exception e) {
      log.warn("Bootstrap config table already exists or could not be created: {}", e.getMessage());
    }
  }

  @PostConstruct
  public void initRoles() {
    try {
      for (Role role : Role.values()) {
        if (roleRepository.findByName(role).isEmpty()) {
          RoleEntity roleEntity = RoleEntity.builder().name(role).build();
          roleRepository.save(roleEntity);
          log.info("Role created: {}", role);
        }
      }
    } catch (Exception e) {
      log.warn("Roles already exist or could not be created: {}", e.getMessage());
    }
  }

  @Transactional
  public void bootstrapAdminIfNeeded() {
    boolean alreadyBootstrapped =
        bootstrapConfigRepository.existsByKey(BOOTSTRAP_KEY)
            && "true"
                .equals(
                    bootstrapConfigRepository
                        .findByKey(BOOTSTRAP_KEY)
                        .map(BootstrapConfigEntity::getValue)
                        .orElse("false"));

    if (alreadyBootstrapped) {
      log.info("Admin already bootstrapped, no action needed.");
      return;
    }

    boolean adminExists = userRepository.findByEmail(adminEmail).isPresent();

    if (adminExists) {
      log.info("Admin already exists with email {}. Updating bootstrap flag.", adminEmail);
      bootstrapConfigRepository
          .findByKey(BOOTSTRAP_KEY)
          .ifPresent(
              config -> {
                config.setValue("true");
                bootstrapConfigRepository.save(config);
              });
      return;
    }

    log.info("No admin found. Bootstrapping first administrator...");

    RoleEntity adminRole =
        roleRepository
            .findByName(Role.ADMIN)
            .orElseThrow(() -> new RuntimeException("ADMIN role not found in database"));

    UserEntity adminUser =
        UserEntity.builder()
            .email(adminEmail)
            .fullName(adminFullName)
            .password(passwordEncoder.encode(adminPassword))
            .role(adminRole)
            .build();

    userRepository.save(adminUser);

    bootstrapConfigRepository
        .findByKey(BOOTSTRAP_KEY)
        .ifPresentOrElse(
            config -> {
              config.setValue("true");
              bootstrapConfigRepository.save(config);
            },
            () -> {
              BootstrapConfigEntity newConfig =
                  BootstrapConfigEntity.builder()
                      .key(BOOTSTRAP_KEY)
                      .value("true")
                      .createdAt(Instant.now())
                      .build();
              bootstrapConfigRepository.save(newConfig);
            });

    log.info("Admin created successfully!");
    log.info("Email: {}", adminEmail);
    log.info("Password: {}", adminPassword);
    log.info("Change the password immediately after first login!");
  }
}
