package hei.school.nmn.endpoint.rest.controller.controller;

import hei.school.nmn.entity.Movie;
import hei.school.nmn.service.AuthorizationService;
import hei.school.nmn.service.MovieService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final AuthorizationService authorizationService;

    @PutMapping("/movies/{id}")
    public Movie updateById(
            @RequestHeader UUID userId,
            @PathVariable UUID id,
            @RequestBody Movie movie) {
        authorizationService.requireManager(userId);
        return movieService.update(id, movie);
    }

    @PutMapping("/movies")
    public Movie update(
            @RequestHeader UUID userId,
            @RequestBody Movie movie) {
        authorizationService.requireManager(userId);
        if (movie.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie id is required");
        }
        return movieService.update(movie.id(), movie);
    }
}
