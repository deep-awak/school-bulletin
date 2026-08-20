package api.poja.app.unit.util;

import api.poja.app.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

	private IdGenerator idGenerator;

	@BeforeEach
	void setUp() {
		idGenerator = new IdGenerator();
	}

	@Test
	void generateStudentId_shouldReturnCorrectFormat() {
		String id = idGenerator.generateStudentId();
		int year = Year.now().getValue() % 100;
		String expectedPrefix = "STD" + String.format("%02d", year);

		assertNotNull(id);
		assertTrue(id.startsWith(expectedPrefix));
		assertEquals(9, id.length());
	}

	@Test
	void generateStudentId_shouldIncrementSequentially() {
		String id1 = idGenerator.generateStudentId();
		String id2 = idGenerator.generateStudentId();

		assertNotEquals(id1, id2);
		int seq1 = Integer.parseInt(id1.substring(6));
		int seq2 = Integer.parseInt(id2.substring(6));
		assertEquals(seq1 + 1, seq2);
	}

	@Test
	void generateStudentId_shouldBeUnique() {
		Set<String> ids = new HashSet<>();
		for (int i = 0; i < 100; i++) {
			ids.add(idGenerator.generateStudentId());
		}
		assertEquals(100, ids.size());
	}

	@Test
	void generateTeacherId_shouldReturnCorrectFormat() {
		String id = idGenerator.generateTeacherId();
		int year = Year.now().getValue() % 100;
		String expectedPrefix = "TCH" + String.format("%02d", year);

		assertNotNull(id);
		assertTrue(id.startsWith(expectedPrefix));
		assertEquals(8, id.length());
	}

	@Test
	void generateTeacherId_shouldIncrementSequentially() {
		String id1 = idGenerator.generateTeacherId();
		String id2 = idGenerator.generateTeacherId();

		assertNotEquals(id1, id2);
		int seq1 = Integer.parseInt(id1.substring(5));
		int seq2 = Integer.parseInt(id2.substring(5));
		assertEquals(seq1 + 1, seq2);
	}

	@Test
	void generateGroupId_shouldReturnCorrectFormat() {
		String id1 = idGenerator.generateGroupId();
		String id2 = idGenerator.generateGroupId();

		assertNotNull(id1);
		assertNotNull(id2);
		assertTrue(id1.matches("[A-Z]\\d+"));
		assertTrue(id2.matches("[A-Z]\\d+"));
	}

	@Test
	void generateGroupId_shouldCycleLetters() {
		Set<String> ids = new HashSet<>();
		for (int i = 0; i < 30; i++) {
			ids.add(idGenerator.generateGroupId());
		}
		assertEquals(30, ids.size());
		assertTrue(ids.stream().anyMatch(id -> id.startsWith("A")));
		assertTrue(ids.stream().anyMatch(id -> id.startsWith("B")));
		assertTrue(ids.stream().anyMatch(id -> id.startsWith("C")));
		assertTrue(ids.stream().anyMatch(id -> id.startsWith("D")));
	}

	@Test
	void generateGroupId_shouldGenerateExpectedSequence() {
		idGenerator = new IdGenerator();
		assertEquals("A1", idGenerator.generateGroupId());
		assertEquals("A2", idGenerator.generateGroupId());
		assertEquals("A3", idGenerator.generateGroupId());
		for (int i = 0; i < 23; i++) {
			idGenerator.generateGroupId();
		}
		assertEquals("B1", idGenerator.generateGroupId());
	}
}