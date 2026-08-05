package hei.school.nmn.mapper;

import hei.school.nmn.entity.Movie;
import hei.school.nmn.repository.model.JMovie;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

  public static JMovie toJ(Movie movie) {
    return JMovie.builder()
        .id(movie.id())
        .title(movie.title())
        .genre(movie.genre())
        .description(movie.description())
        .duration(movie.duration())
        .build();
  }

  public static Movie toDomain(JMovie movie) {
    return Movie.builder()
        .id(movie.getId())
        .title(movie.getTitle())
        .genre(movie.getGenre())
        .description(movie.getDescription())
        .duration(movie.getDuration())
        .projection(
            movie.getProjections().stream()
                .map(ProjectionMapper::toDomain)
                .collect(Collectors.toList()))
        .build();
  }

  public static Movie shallow(JMovie movie) {
    return Movie.builder()
        .id(movie.getId())
        .title(movie.getTitle())
        .genre(movie.getGenre())
        .description(movie.getDescription())
        .duration(movie.getDuration())
        .projection(List.of())
        .build();
  }
}
