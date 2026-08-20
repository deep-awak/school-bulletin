package api.poja.app.integration;

import api.poja.app.PojaApplication;
import api.poja.app.jpa.RoleEntity;
import api.poja.app.jpa.UserEntity;
import api.poja.app.model.Role;
import api.poja.app.repository.RoleRepository;
import api.poja.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@SpringBootTest(classes = PojaApplication.class)
@AutoConfigureMockMvc
@Testcontainers
public abstract class BaseIntegrationTest {

	@Container
	protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected RoleRepository roleRepository;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Value("${app.jwt.secret:testSecretKeyForIntegrationTests}")
	protected String jwtSecret;

	protected UUID adminUserId;
	protected String adminToken;
	protected UUID promotionId;
	protected String groupId;
	protected String teacherId;
	protected String studentId;
	protected UUID courseId;
	protected UUID gradeId;

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.flyway.enabled", () -> "true");
		registry.add("app.jwt.secret", () -> "testSecretKeyForIntegrationTests");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@BeforeEach
	void setUpBase() throws Exception {
		userRepository.deleteAll();
		createAdminUser();
		createBaseTestData();
	}

	private void createAdminUser() {
		RoleEntity adminRole = roleRepository.findByName(Role.ADMIN)
				.orElseThrow(() -> new RuntimeException("ADMIN role not found"));

		UserEntity adminUser = UserEntity.builder()
				.email("admin@test.com")
				.fullName("Admin Test")
				.password(passwordEncoder.encode("Admin123!"))
				.role(adminRole)
				.build();

		adminUser = userRepository.save(adminUser);
		adminUserId = adminUser.getId();
		adminToken = "Bearer " + generateJwtToken(adminUser);
	}

	protected String generateJwtToken(UserEntity user) {
		return Jwts.builder()
				.setSubject(user.getEmail())
				.claim("userId", user.getId().toString())
				.claim("role", user.getRole().getName().name())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000))
				.signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
				.compact();
	}

	protected abstract void createBaseTestData() throws Exception;
}