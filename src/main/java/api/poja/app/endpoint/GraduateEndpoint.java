package api.poja.app.endpoint;

import api.poja.app.dto.GraduateRowDto;
import api.poja.app.excel.GraduateExcelExporter;
import api.poja.app.security.AuthContext;
import api.poja.app.service.GraduateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/promotions/{promotionId}/graduates")
@RequiredArgsConstructor
public class GraduateEndpoint {

  private final GraduateService graduateService;
  private final GraduateExcelExporter excelExporter;
  private final AuthContext authContext;

  @GetMapping
  public List<GraduateRowDto> ranking(@PathVariable UUID promotionId) {
    var requester = authContext.getCurrentUser();
    return graduateService.rankPromotion(promotionId, requester);
  }

  @GetMapping(value = "/export", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> exportExcel(@PathVariable UUID promotionId) throws IOException {
    var requester = authContext.getCurrentUser();
    List<GraduateRowDto> rows = graduateService.rankPromotion(promotionId, requester);
    byte[] excel = excelExporter.toBytes(rows);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"promotion-" + promotionId + "-graduates.xlsx\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel);
  }

  @GetMapping("/results")
  public Map<UUID, List<GraduateRowDto>> threeYearResults(@RequestParam List<UUID> promotionIds) {
    var requester = authContext.getCurrentUser();
    return graduateService.rankPromotions(promotionIds, requester);
  }
}