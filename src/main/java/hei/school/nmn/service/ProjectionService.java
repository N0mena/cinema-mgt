package hei.school.nmn.service;

import hei.school.nmn.entity.Projection;
import hei.school.nmn.mapper.ProjectionMapper;
import hei.school.nmn.repository.MovieRepository;
import hei.school.nmn.repository.ProjectionRepository;
import hei.school.nmn.repository.RoomRepository;
import hei.school.nmn.repository.model.JMovie;
import hei.school.nmn.repository.model.JProjection;
import hei.school.nmn.repository.model.JRoom;
import hei.school.nmn.service.exception.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProjectionService {

  private final ProjectionRepository projectionRepository;
  private final MovieRepository movieRepository;
  private final RoomRepository roomRepository;

  @Transactional
  public Projection create(Projection projection) {
    JMovie movie =
        movieRepository
            .findById(projection.movie().id())
            .orElseThrow(
                () -> new NotFoundException("Movie not found: " + projection.movie().id()));
    JRoom room =
        roomRepository
            .findById(projection.room().id())
            .orElseThrow(() -> new NotFoundException("Room not found: " + projection.room().id()));
    JProjection saved = projectionRepository.save(ProjectionMapper.toJ(projection, movie, room));
    return ProjectionMapper.toDomain(saved);
  }

  @Transactional(readOnly = true)
  public List<Projection> getAll() {
    return projectionRepository.findAll().stream()
        .map(ProjectionMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional
  public Projection update(UUID id, Projection projection) {
    JProjection existing =
        projectionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Projection not found: " + id));
    JMovie movie =
        movieRepository
            .findById(projection.movie().id())
            .orElseThrow(
                () -> new NotFoundException("Movie not found: " + projection.movie().id()));
    JRoom room =
        roomRepository
            .findById(projection.room().id())
            .orElseThrow(() -> new NotFoundException("Room not found: " + projection.room().id()));
    existing.setDatetime(projection.datetime());
    existing.setSeatPrice(projection.seatPrice());
    existing.setMovie(movie);
    existing.setRoom(room);
    return ProjectionMapper.toDomain(existing);
  }

  @Transactional(readOnly = true)
  public Projection getById(UUID id) {
    return projectionRepository
        .findById(id)
        .map(ProjectionMapper::toDomain)
        .orElseThrow(() -> new NotFoundException("Projection not found: " + id));
  }

  @Transactional(readOnly = true)
  public List<Projection> getByMovie(UUID movieId) {
    return projectionRepository.findByMovie_Id(movieId).stream()
        .map(ProjectionMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Projection> getUpcoming() {
    return projectionRepository.findByDatetimeAfter(Instant.now()).stream()
        .map(ProjectionMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional
  public void delete(UUID id) {
    if (!projectionRepository.existsById(id)) {
      throw new NotFoundException("Projection not found: " + id);
    }
    projectionRepository.deleteById(id);
  }
}
