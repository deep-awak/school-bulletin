package api.poja.app.integration.security;

import api.poja.app.integration.BaseIntegrationTest;
import api.poja.app.jpa.RoleEntity;
import api.poja.app.jpa.UserEntity;
import api.poja.app.model.Role;
import api.poja.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
class SecurityIT extends BaseIntegrationTest {

	private final UserRepository userRepository;

	private UUID promotionId;
	private String studentToken;
	private String teacherToken;



	@Override
	protected void createBaseTestData() throws Exception {
		String promoRequest = "{\"name\":\"Promotion Security\",\"startYear\":2023,\"endYear\":2026}";
		promotionId = UUID.fromString(objectMapper.readTree(
				mockMvc.perform(post("/api/promotions")
								.header("Authorization", adminToken)
								.contentType(MediaType.APPLICATION_JSON)
								.content(promoRequest))
						.andExpect(status().isCreated())
						.andReturn().getResponse().getContentAsString()
		).get("id").asText());

		RoleEntity studentRole = roleRepository.findByName(Role.STUDENT).orElseThrow();
		UserEntity studentUser = UserEntity.builder()
				.email("student.security@hei.mg")
				.fullName("Student Security")
				.password(passwordEncoder.encode("Student123!"))
				.role(studentRole)
				.studentId("STD24185")
				.build();
		userRepository.save(studentUser);
		studentToken = "Bearer " + generateJwtToken(studentUser);

		RoleEntity teacherRole = roleRepository.findByName(Role.TEACHER).orElseThrow();
		UserEntity teacherUser = UserEntity.builder()
				.email("teacher.security@hei.mg")
				.fullName("Teacher Security")
				.password(passwordEncoder.encode("Teacher123!"))
				.role(teacherRole)
				.teacherId("TCH24001")
				.build();
		userRepository.save(teacherUser);
		teacherToken = "Bearer " + generateJwtToken(teacherUser);
	}

	@Test
	void admin_shouldAccessAllEndpoints() throws Exception {
		String studentRequest = "{\"std\":\"HEI-SEC-001\",\"firstName\":\"Sec\",\"lastName\":\"Test\",\"email\":\"sec.test@hei.mg\",\"promotionId\":\"" + promotionId + "\",\"groupId\":\"J1\"}";
		mockMvc.perform(post("/api/students")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(studentRequest))
				.andExpect(status().isCreated());

		String teacherRequest = "{\"firstName\":\"Sec\",\"lastName\":\"Teacher\",\"email\":\"sec.teacher@hei.mg\"}";
		mockMvc.perform(post("/api/teachers")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(teacherRequest))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/promotions/{promotionId}/graduates", promotionId)
						.header("Authorization", adminToken))
				.andExpect(status().isOk());
	}

	@Test
	void student_shouldOnlyAccessOwnData() throws Exception {
		mockMvc.perform(get("/api/students/{id}", "STD24185")
						.header("Authorization", studentToken))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/students/{id}", "STD24186")
						.header("Authorization", studentToken))
				.andExpect(status().isForbidden());

		String studentRequest = "{\"std\":\"HEI-SEC-002\",\"firstName\":\"Sec2\",\"lastName\":\"Test2\",\"email\":\"sec2.test@hei.mg\",\"promotionId\":\"" + promotionId + "\",\"groupId\":\"J1\"}";
		mockMvc.perform(post("/api/students")
						.header("Authorization", studentToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(studentRequest))
				.andExpect(status().isForbidden());

		mockMvc.perform(patch("/api/grades/{id}", UUID.randomUUID())
						.header("Authorization", studentToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"value\":16.0,\"reason\":\"Test\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void teacher_shouldOnlyAccessOwnCourses() throws Exception {
		mockMvc.perform(get("/api/grades")
						.param("studentId", "STD24185")
						.header("Authorization", teacherToken))
				.andExpect(status().isOk());

		mockMvc.perform(patch("/api/grades/{id}", UUID.randomUUID())
						.header("Authorization", teacherToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"value\":16.0,\"reason\":\"Test\"}"))
				.andExpect(status().isForbidden());

		String studentRequest = "{\"std\":\"HEI-SEC-003\",\"firstName\":\"Sec3\",\"lastName\":\"Test3\",\"email\":\"sec3.test@hei.mg\",\"promotionId\":\"" + promotionId + "\",\"groupId\":\"J1\"}";
		mockMvc.perform(post("/api/students")
						.header("Authorization", teacherToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(studentRequest))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/promotions/{promotionId}/graduates", promotionId)
						.header("Authorization", teacherToken))
				.andExpect(status().isForbidden());
	}

	@Test
	void unauthenticatedUser_shouldBeRejected() throws Exception {
		mockMvc.perform(get("/api/students")
						.param("promotionId", promotionId.toString()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void invalidToken_shouldBeRejected() throws Exception {
		mockMvc.perform(get("/api/students")
						.header("Authorization", "Bearer invalidToken")
						.param("promotionId", promotionId.toString()))
				.andExpect(status().isUnauthorized());
	}
}