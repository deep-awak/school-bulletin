package api.poja.app.integration.endpoint;

import api.poja.app.dto.request.CreatePromotionRequest;
import api.poja.app.dto.request.CreateStudentRequest;
import api.poja.app.integration.BaseIntegrationTest;
import api.poja.app.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StudentEndpointIT extends BaseIntegrationTest {

	private StudentRepository studentRepository;

	StudentEndpointIT(StudentRepository studentRepository) {
		this.studentRepository = studentRepository;
	}

	@Override
	protected void createBaseTestData() throws Exception {
		CreatePromotionRequest promoRequest = CreatePromotionRequest.builder()
				.name("Promotion Test")
				.startYear(2023)
				.endYear(2026)
				.build();

		MvcResult promoResult = mockMvc.perform(post("/api/promotions")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(promoRequest)))
				.andExpect(status().isCreated())
				.andReturn();

		promotionId = UUID.fromString(objectMapper.readTree(promoResult.getResponse().getContentAsString())
				.get("id").asText());

		String groupRequest = "{\"name\":\"Groupe A1\",\"promotionId\":\"" + promotionId + "\"}";
		MvcResult groupResult = mockMvc.perform(post("/api/groups")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(groupRequest))
				.andExpect(status().isCreated())
				.andReturn();

		groupId = objectMapper.readTree(groupResult.getResponse().getContentAsString())
				.get("id").asText();
	}

	@BeforeEach
	void setUp() {
		studentRepository.deleteAll();
	}

	@Test
	void createStudent_shouldReturn201AndAutoGenerateId() throws Exception {
		CreateStudentRequest request = CreateStudentRequest.builder()
				.std("HEI-TEST-001")
				.firstName("Jean")
				.lastName("Rakoto")
				.email("jean.test@hei.mg")
				.promotionId(promotionId)
				.groupId(groupId)
				.build();

		MvcResult result = mockMvc.perform(post("/api/students")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.std").value("HEI-TEST-001"))
				.andReturn();

		String studentId = objectMapper.readTree(result.getResponse().getContentAsString())
				.get("id").asText();

		assertTrue(studentId.startsWith("STD"));
		assertEquals(9, studentId.length());
	}

	@Test
	void createStudent_shouldReturn400WhenValidationFails() throws Exception {
		CreateStudentRequest invalidRequest = CreateStudentRequest.builder()
				.std(null)
				.firstName("Jean")
				.build();

		mockMvc.perform(post("/api/students")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getStudentById_shouldReturn200WhenStudentExists() throws Exception {
		CreateStudentRequest request = CreateStudentRequest.builder()
				.std("HEI-TEST-002")
				.firstName("Marie")
				.lastName("Rabe")
				.email("marie.test@hei.mg")
				.promotionId(promotionId)
				.groupId(groupId)
				.build();

		MvcResult createResult = mockMvc.perform(post("/api/students")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn();

		String studentId = objectMapper.readTree(createResult.getResponse().getContentAsString())
				.get("id").asText();

		mockMvc.perform(get("/api/students/{id}", studentId)
						.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(studentId))
				.andExpect(jsonPath("$.firstName").value("Marie"));
	}

	@Test
	void getStudentById_shouldReturn404WhenStudentNotFound() throws Exception {
		mockMvc.perform(get("/api/students/{id}", "STD99999")
						.header("Authorization", adminToken))
				.andExpect(status().isNotFound());
	}

	@Test
	void listStudentsByPromotion_shouldReturn200() throws Exception {
		for (int i = 0; i < 3; i++) {
			CreateStudentRequest request = CreateStudentRequest.builder()
					.std("HEI-TEST-" + String.format("%03d", i))
					.firstName("Student" + i)
					.lastName("Test" + i)
					.email("student" + i + ".test@hei.mg")
					.promotionId(promotionId)
					.groupId(groupId)
					.build();

			mockMvc.perform(post("/api/students")
							.header("Authorization", adminToken)
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isCreated());
		}

		mockMvc.perform(get("/api/students")
						.param("promotionId", promotionId.toString())
						.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3));
	}

	@Test
	void reassignGroup_shouldReturn201() throws Exception {
		CreateStudentRequest request = CreateStudentRequest.builder()
				.std("HEI-TEST-003")
				.firstName("Pierre")
				.lastName("Andri")
				.email("pierre.test@hei.mg")
				.promotionId(promotionId)
				.groupId(groupId)
				.build();

		MvcResult createResult = mockMvc.perform(post("/api/students")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andReturn();

		String studentId = objectMapper.readTree(createResult.getResponse().getContentAsString())
				.get("id").asText();

		String newGroupRequest = "{\"name\":\"Groupe B2\",\"promotionId\":\"" + promotionId + "\"}";
		MvcResult groupResult = mockMvc.perform(post("/api/groups")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(newGroupRequest))
				.andExpect(status().isCreated())
				.andReturn();

		String newGroupId = objectMapper.readTree(groupResult.getResponse().getContentAsString())
				.get("id").asText();

		String reassignRequest = "{\"studentId\":\"" + studentId + "\",\"groupId\":\"" + newGroupId + "\",\"startDate\":\"2025-09-01\"}";

		mockMvc.perform(post("/api/students/group-assignments")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(reassignRequest))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/students/{id}/group-history", studentId)
						.header("Authorization", adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2));
	}
}