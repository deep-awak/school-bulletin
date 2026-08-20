package api.poja.app.endpoint;

import api.poja.app.dto.GraduateRowDto;
import api.poja.app.excel.GraduateExcelExporter;
import api.poja.app.service.GraduateService;
import api.poja.app.service.PromotionService;
import api.poja.app.service.StudentService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PromotionViewEndpoint {

  private final PromotionService promotionService;
  private final StudentService studentService;
  private final GraduateService graduateService;
  private final GraduateExcelExporter excelExporter;

  @GetMapping("/promotions")
  public String listPromotions(Model model) {
    model.addAttribute("promotions", promotionService.listAll());
    return "promotions";
  }

  @GetMapping("/promotions/{id}")
  public String promotionDetail(@PathVariable UUID id, Model model) {
    var promotion = promotionService.getById(id);
    var students = studentService.listByPromotion(id);
    model.addAttribute("promotion", promotion);
    model.addAttribute("students", students);
    return "promotion-detail";
  }

  @GetMapping(
      value = "/promotions/{id}/export",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> exportExcel(@PathVariable UUID id) throws IOException {
    List<GraduateRowDto> rows = graduateService.rankPromotionForView(id);
    byte[] excel = excelExporter.toBytes(rows);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"promotion-" + id + "-graduates.xlsx\"")
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(excel);
  }
}
