package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.entity.Movie;
import hei.school.nmn.service.MovieService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class MovieController {

  private final MovieService movieService;

  @GetMapping("/movies")
  public List<Movie> getAll() {
    return movieService.getAll();
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PutMapping("/movies/{id}")
  public Movie updateById(@PathVariable UUID id, @RequestBody Movie movie) {
    return movieService.update(id, movie);
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PutMapping("/movies")
  public Movie update(@RequestBody Movie movie) {
    if (movie.id() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie id is required");
    }
    return movieService.update(movie.id(), movie);
  }
}
