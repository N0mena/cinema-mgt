package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.entity.Projection;
import hei.school.nmn.service.ProjectionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ProjectionController {
  private final ProjectionService projectionService;

  @GetMapping("/projections")
  public List<Projection> getProjection() {
    return projectionService.getAll();
  }

  @PutMapping("/projection")
  public Projection create(@RequestBody Projection projection) {
    return projectionService.create(projection);
  }
}
