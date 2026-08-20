package api.poja.app.unit.validator;

import api.poja.app.dto.request.CreatePromotionRequest;
import api.poja.app.exception.ValidationException;
import api.poja.app.validator.PromotionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromotionValidatorTest {

	private PromotionValidator validator;
	private CreatePromotionRequest validRequest;

	@BeforeEach
	void setUp() {
		validator = new PromotionValidator();
		validRequest = CreatePromotionRequest.builder()
				.name("Promotion 2026")
				.startYear(2023)
				.endYear(2026)
				.build();
	}

	@Test
	void validate_shouldNotThrowWhenRequestIsValid() {
		assertDoesNotThrow(() -> validator.validate(validRequest));
	}

	@Test
	void validate_shouldThrowWhenRequestIsNull() {
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(null));
		assertEquals("Request must not be null", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenNameIsNull() {
		validRequest.setName(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Promotion name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenNameIsBlank() {
		validRequest.setName("   ");
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Promotion name is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenStartYearIsNull() {
		validRequest.setStartYear(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Promotion start year is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenEndYearIsNull() {
		validRequest.setEndYear(null);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Promotion end year is required", ex.getMessage());
	}

	@Test
	void validate_shouldThrowWhenEndYearBeforeStartYear() {
		validRequest.setStartYear(2025);
		validRequest.setEndYear(2023);
		ValidationException ex = assertThrows(ValidationException.class,
				() -> validator.validate(validRequest));
		assertEquals("Promotion end year must be after start year", ex.getMessage());
	}
}