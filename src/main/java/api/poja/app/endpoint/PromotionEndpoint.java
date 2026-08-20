package api.poja.app.endpoint;

import api.poja.app.dto.request.CreatePromotionRequest;
import api.poja.app.dto.PromotionDto;
import api.poja.app.mapper.PromotionMapper;
import api.poja.app.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionEndpoint {

  private final PromotionService promotionService;

  @PostMapping
  public ResponseEntity<PromotionDto> create(@RequestBody CreatePromotionRequest request) {
    var created = promotionService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(PromotionMapper.toDto(created));
  }

  @GetMapping("/{id}")
  public PromotionDto getById(@PathVariable UUID id) {
    return PromotionMapper.toDto(promotionService.getById(id));
  }

  @GetMapping
  public List<PromotionDto> listAll() {
    return promotionService.listAll().stream()
            .map(PromotionMapper::toDto)
            .toList();
  }
}