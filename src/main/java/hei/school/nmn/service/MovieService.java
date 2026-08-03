package hei.school.nmn.service;

import hei.school.nmn.entity.Movie;
import hei.school.nmn.repository.model.JMovie;
import hei.school.nmn.mapper.MovieMapper;
import hei.school.nmn.repository.MovieRepository;
import hei.school.nmn.service.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;

  @Transactional(readOnly = true)
  public List<Movie> getAll() {
    return movieRepository.findAll().stream()
        .map(MovieMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Movie getById(UUID id) {
    return MovieMapper.toDomain(getJMovie(id));
  }

  @Transactional
  public Movie create(Movie movie) {
    return MovieMapper.toDomain(movieRepository.save(MovieMapper.toJ(movie)));
  }

  @Transactional
  public Movie update(UUID id, Movie movie) {
    JMovie existing = getJMovie(id);
    existing.setTitle(movie.title());
    existing.setGenre(movie.genre());
    existing.setDescription(movie.description());
    existing.setDuration(movie.duration());
    return MovieMapper.toDomain(existing);
  }

  @Transactional
  public void delete(UUID id) {
    if (!movieRepository.existsById(id)) {
      throw new NotFoundException("Movie not found: " + id);
    }
    movieRepository.deleteById(id);
  }

  private JMovie getJMovie(UUID id) {
    return movieRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Movie not found: " + id));
  }
}
