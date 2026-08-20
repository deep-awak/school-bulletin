package api.poja.app.excel;

import api.poja.app.dto.GraduateRowDto;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/** Builds the mandatory graduate ranking export: Rang / STD / Nom / Prénom / Moyenne générale. */
@Component
public class GraduateExcelExporter {

  private static final String[] HEADERS = {"Rang", "STD", "Nom", "Prénom", "Moyenne générale"};

  public byte[] toBytes(List<GraduateRowDto> rows) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      writeSheet(workbook, rows);
      workbook.write(out);
      return out.toByteArray();
    }
  }

  public File toFile(List<GraduateRowDto> rows, String fileName) throws IOException {
    File file = File.createTempFile(fileName, ".xlsx");
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream out = new FileOutputStream(file)) {
      writeSheet(workbook, rows);
      workbook.write(out);
    }
    return file;
  }

  private void writeSheet(XSSFWorkbook workbook, List<GraduateRowDto> rows) {
    XSSFSheet sheet = workbook.createSheet("Diplômés");

    Row header = sheet.createRow(0);
    for (int i = 0; i < HEADERS.length; i++) {
      Cell cell = header.createCell(i);
      cell.setCellValue(HEADERS[i]);
    }

    int rowIndex = 1;
    for (GraduateRowDto row : rows) {
      Row excelRow = sheet.createRow(rowIndex++);
      excelRow.createCell(0).setCellValue(row.getRank());
      excelRow.createCell(1).setCellValue(row.getStd());
      excelRow.createCell(2).setCellValue(row.getLastName());
      excelRow.createCell(3).setCellValue(row.getFirstName());
      excelRow.createCell(4).setCellValue(row.getAverage());
    }

    for (int i = 0; i < HEADERS.length; i++) {
      sheet.autoSizeColumn(i);
    }
  }
}
