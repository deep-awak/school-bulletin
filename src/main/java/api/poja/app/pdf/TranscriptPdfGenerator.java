package api.poja.app.pdf;

import api.poja.app.model.Course;
import api.poja.app.model.Grade;
import api.poja.app.model.Promotion;
import api.poja.app.model.Student;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TranscriptPdfGenerator {

  public File generate(
		  Student student,
		  Promotion promotion,
		  String academicYear,
		  List<Grade> grades,
		  Map<UUID, Course> coursesById)
          throws IOException {
    File file = File.createTempFile("transcript-" + student.getStd() + "-", ".pdf");

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      try (PDPageContentStream content = new PDPageContentStream(document, page)) {
        PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
        PDType1Font bodyFont = PDType1Font.HELVETICA;
        float y = 780;

        y = writeLine(content, titleFont, 16, 50, y, "Relevé de notes");
        y -= 10;
        y = writeLine(content, bodyFont, 11, 50, y, "Étudiant : " + student.getFirstName() + " " + student.getLastName());
        y = writeLine(content, bodyFont, 11, 50, y, "STD : " + student.getStd());
        y = writeLine(content, bodyFont, 11, 50, y, "Promotion : " + promotion.getName());
        y = writeLine(content, bodyFont, 11, 50, y, "Année académique : " + academicYear);
        y -= 15;

        y = writeLine(content, titleFont, 12, 50, y, "Cours et notes");
        y -= 5;

        double sum = 0;
        int count = 0;
        for (Grade grade : grades) {
          Course course = coursesById.get(grade.getCourseId());
          String courseLabel = course != null ? course.getName() + " (" + course.getCode() + ")" : "Cours #" + grade.getCourseId();
          y = writeLine(content, bodyFont, 11, 60, y, courseLabel + " : " + grade.getValue() + "/20");
          sum += grade.getValue();
          count++;
        }

        y -= 15;
        double average = count > 0 ? sum / count : 0;
        writeLine(content, titleFont, 12, 50, y, String.format("Moyenne générale : %.2f/20", average));
      }

      document.save(file);
    }

    return file;
  }

  private float writeLine(
          PDPageContentStream content, PDType1Font font, float size, float x, float y, String text)
          throws IOException {
    content.beginText();
    content.setFont(font, size);
    content.newLineAtOffset(x, y);
    content.showText(text);
    content.endText();
    return y - (size + 6);
  }
}