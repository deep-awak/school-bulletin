package api.poja.app.endpoint;

import api.poja.app.dto.GraduateRowDto;
import api.poja.app.excel.GraduateExcelExporter;
import api.poja.app.service.GraduateService;
import api.poja.app.service.GroupService;
import api.poja.app.service.PromotionService;
import api.poja.app.service.StudentService;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Minimal Thymeleaf UI (no login, no CSS framework): list promotions, drill into one, see its
 * students, and download the Excel export from the browser.
 *
 * <p>This view has no authentication of its own (out of scope for this exercise, see README), so it
 * cannot send the {@code X-User-Id} header the REST API requires. Its Excel download therefore goes
 * through {@link GraduateService#rankPromotionForView}, a role-check-free path meant only for this
 * controller - the REST endpoint under {@code /api/...} still enforces ADMIN.
 */
@Controller
@AllArgsConstructor
public class PromotionViewEndpoint {
  private final PromotionService promotionService;
  private final StudentService studentService;
  private final GroupService groupService;
  private final GraduateService graduateService;
  private final GraduateExcelExporter excelExporter;

  @GetMapping("/promotions")
  public String listPromotions(Model model) {
    model.addAttribute("promotions", promotionService.listAll());
    return "promotions";
  }

  @GetMapping("/promotions/{id}")
  public String promotionDetail(@PathVariable Long id, Model model) {
    var promotion = promotionService.getById(id);
    var students = studentService.listByPromotion(id);
    model.addAttribute("promotion", promotion);
    model.addAttribute("students", students);
    return "promotion-detail";
  }

  @GetMapping(
      value = "/promotions/{id}/export",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> exportExcel(@PathVariable Long id) throws IOException {
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
