package api.poja.app.endpoint;

import api.poja.app.dto.GraduateRowDto;
import api.poja.app.excel.GraduateExcelExporter;
import api.poja.app.security.AuthContext;
import api.poja.app.service.GraduateService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions/{promotionId}/graduates")
@AllArgsConstructor
public class GraduateEndpoint {
  private final GraduateService graduateService;
  private final GraduateExcelExporter excelExporter;
  private final AuthContext authContext;

  @GetMapping
  public List<GraduateRowDto> ranking(
      @PathVariable Long promotionId, HttpServletRequest httpRequest) {
    var requester = authContext.resolve(httpRequest);
    return graduateService.rankPromotion(promotionId, requester);
  }

  @GetMapping(
      value = "/export",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> exportExcel(
      @PathVariable Long promotionId, HttpServletRequest httpRequest) throws IOException {
    var requester = authContext.resolve(httpRequest);
    List<GraduateRowDto> rows = graduateService.rankPromotion(promotionId, requester);
    byte[] excel = excelExporter.toBytes(rows);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"promotion-" + promotionId + "-graduates.xlsx\"")
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(excel);
  }

  @GetMapping("/results")
  public Map<Long, List<GraduateRowDto>> threeYearResults(
      @RequestParam List<Long> promotionIds, HttpServletRequest httpRequest) {
    var requester = authContext.resolve(httpRequest);
    return graduateService.rankPromotions(promotionIds, requester);
  }
}
