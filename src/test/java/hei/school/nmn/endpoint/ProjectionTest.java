package hei.school.nmn.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import hei.school.nmn.conf.FacadeIT;
import hei.school.nmn.conf.JwtTestFactory;
import hei.school.nmn.entity.Movie;
import hei.school.nmn.entity.Projection;
import hei.school.nmn.entity.Room;
import hei.school.nmn.entity.enums.Genre;
import hei.school.nmn.entity.enums.UserRole;
import hei.school.nmn.repository.MovieRepository;
import hei.school.nmn.repository.ProjectionRepository;
import hei.school.nmn.repository.RoomRepository;
import hei.school.nmn.repository.model.JProjection;
import hei.school.nmn.service.MovieService;
import hei.school.nmn.service.ProjectionService;
import hei.school.nmn.service.RoomService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class ProjectionTest extends FacadeIT {

  @Autowired private MovieService movieService;
  @Autowired private RoomService roomService;
  @Autowired private ProjectionService projectionService;

  @Autowired private MovieRepository movieRepository;
  @Autowired private RoomRepository roomRepository;
  @Autowired private ProjectionRepository projectionRepository;

  @Autowired private TestRestTemplate restClient;
  @Autowired private JwtTestFactory jwtTestFactory;

  @BeforeEach
  void cleanDatabase() {
    projectionRepository.deleteAll();
    roomRepository.deleteAll();
    movieRepository.deleteAll();
  }

  @Test
  void shouldGetProjections() {
    Projection projection = createProjection();

    ResponseEntity<String> response = restClient.getForEntity("/projections", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains(projection.id().toString());
    assertThat(projectionService.getAll())
        .extracting(Projection::id)
        .containsExactly(projection.id());
  }

  @Test
  void shouldGetProjectionById() {
    Projection projection = createProjection();

    Projection found = projectionService.getById(projection.id());

    assertThat(found.id()).isEqualTo(projection.id());
    assertThat(found.seatPrice()).isEqualByComparingTo(BigDecimal.valueOf(12.50));
  }

  @Test
  void shouldCreateProjection() {
    UUID movieId = createMovie();
    UUID roomId = createRoom();
    String body =
        "{"
            + "\"datetime\":\"2026-08-10T20:00:00Z\","
            + "\"seatPrice\":12.50,"
            + "\"movie\":{\"id\":\""
            + movieId
            + "\"},"
            + "\"room\":{\"id\":\""
            + roomId
            + "\"}"
            + "}";

    ResponseEntity<String> response =
        restClient.exchange(
            "/projection", HttpMethod.PUT, new HttpEntity<>(body, jsonHeaders()), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    JProjection saved = projectionRepository.findAll().get(0);
    Projection created = projectionService.getById(saved.getId());
    assertThat(created.datetime()).isEqualTo(Instant.parse("2026-08-10T20:00:00Z"));
    assertThat(created.seatPrice()).isEqualByComparingTo(BigDecimal.valueOf(12.50));
    assertThat(created.movie().id()).isEqualTo(movieId);
    assertThat(created.room().id()).isEqualTo(roomId);
  }

  private HttpHeaders jsonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private Projection createProjection() {
    return projectionService.create(
        Projection.builder()
            .datetime(Instant.parse("2026-08-10T20:00:00Z"))
            .seatPrice(BigDecimal.valueOf(12.50))
            .movie(Movie.builder().id(createMovie()).build())
            .room(Room.builder().id(createRoom()).build())
            .build());
  }

  private UUID createMovie() {
    return movieService
        .create(
            Movie.builder()
                .title("Inception")
                .genre(Genre.ACTION)
                .description("A thief who steals secrets from dreams")
                .duration(Duration.ofHours(2).plusMinutes(28))
                .build())
        .id();
  }

  private UUID createRoom() {
    return roomService.create(Room.builder().number("Room 1").capacity(20).build()).id();
  }
}
