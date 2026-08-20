package api.poja.app.util;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

  private final AtomicLong studentCounter = new AtomicLong(1);
  private final AtomicLong teacherCounter = new AtomicLong(1);
  private final AtomicLong groupCounter = new AtomicLong(1);

  private static final String STUDENT_PREFIX = "STD";
  private static final String TEACHER_PREFIX = "TCH";

  public String generateStudentId() {
    int year = Year.now().getValue() % 100;
    long seq = studentCounter.getAndIncrement();
    return String.format("%s%02d%04d", STUDENT_PREFIX, year, seq);
  }

  public String generateTeacherId() {
    int year = Year.now().getValue() % 100;
    long seq = teacherCounter.getAndIncrement();
    return String.format("%s%02d%03d", TEACHER_PREFIX, year, seq);
  }

  public String generateGroupId() {
    long seq = groupCounter.getAndIncrement();
    char letter = (char) ('A' + (seq - 1) % 26);
    int number = (int) ((seq - 1) / 26) + 1;
    return String.valueOf(letter) + number;
  }
}
