package api.poja.app.endpoint;

import api.poja.app.dto.GroupDto;
import api.poja.app.dto.request.CreateGroupRequest;
import api.poja.app.mapper.GroupMapper;
import api.poja.app.service.GroupService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupEndpoint {
  private final GroupService groupService;

  @PostMapping
  public ResponseEntity<GroupDto> create(@RequestBody CreateGroupRequest request) {
    var created = groupService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(GroupMapper.toDto(created));
  }

  @GetMapping("/{id}")
  public GroupDto getById(@PathVariable String id) {
    return GroupMapper.toDto(groupService.getById(id));
  }

  @GetMapping
  public List<GroupDto> listByPromotion(@RequestParam UUID promotionId) {
    return groupService.listByPromotion(promotionId).stream().map(GroupMapper::toDto).toList();
  }
}
