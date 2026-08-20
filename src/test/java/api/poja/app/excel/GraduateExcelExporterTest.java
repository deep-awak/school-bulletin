package api.poja.app.excel;

import static org.assertj.core.api.Assertions.assertThat;

import api.poja.app.dto.GraduateRowDto;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class GraduateExcelExporterTest {

  private final GraduateExcelExporter exporter = new GraduateExcelExporter();

  @Test
  void generated_workbook_has_the_mandatory_headers_and_one_row_per_graduate() throws Exception {
    var rows =
        List.of(
            GraduateRowDto.builder()
                .rank(1)
                .std("HEI-2023-001")
                .lastName("Rakoto")
                .firstName("Jean")
                .average(17.5)
                .build(),
            GraduateRowDto.builder()
                .rank(2)
                .std("HEI-2023-002")
                .lastName("Rasoa")
                .firstName("Marie")
                .average(15.2)
                .build());

    byte[] bytes = exporter.toBytes(rows);

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      Row header = sheet.getRow(0);

      assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Rang");
      assertThat(header.getCell(1).getStringCellValue()).isEqualTo("STD");
      assertThat(header.getCell(2).getStringCellValue()).isEqualTo("Nom");
      assertThat(header.getCell(3).getStringCellValue()).isEqualTo("Prénom");
      assertThat(header.getCell(4).getStringCellValue()).isEqualTo("Moyenne générale");

      Row firstDataRow = sheet.getRow(1);
      assertThat(firstDataRow.getCell(1).getStringCellValue()).isEqualTo("HEI-2023-001");
      assertThat(firstDataRow.getCell(4).getNumericCellValue()).isEqualTo(17.5);

      assertThat(sheet.getLastRowNum()).isEqualTo(2);
    }
  }

  @Test
  void an_empty_promotion_still_produces_a_workbook_with_just_headers() throws Exception {
    byte[] bytes = exporter.toBytes(List.of());

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      assertThat(sheet.getLastRowNum()).isEqualTo(0);
      assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Rang");
    }
  }
}
