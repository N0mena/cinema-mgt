package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.entity.Projection;
import hei.school.nmn.service.ProjectionService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ProjectionController {
  private final ProjectionService projectionService;

  @GetMapping("/projections")
  public List<Projection> getProjection() {
    return projectionService.getAll();
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PostMapping("/projection")
  public Projection create(@RequestBody Projection projection) {
    return projectionService.create(projection);
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PutMapping("/projections/{id}")
  public Projection update(@PathVariable UUID id, @RequestBody Projection projection) {
    return projectionService.update(id, projection);
  }
}
