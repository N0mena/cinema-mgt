package hei.school.nmn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hei.school.nmn.conf.FacadeIT;
import hei.school.nmn.conf.JwtTestFactory;
import hei.school.nmn.endpoint.dto.request.ReservationRequest;
import hei.school.nmn.endpoint.dto.request.UserRequest;
import hei.school.nmn.endpoint.dto.response.ReservationResponse;
import hei.school.nmn.entity.Movie;
import hei.school.nmn.entity.Projection;
import hei.school.nmn.entity.Room;
import hei.school.nmn.entity.Seat;
import hei.school.nmn.entity.enums.Genre;
import hei.school.nmn.entity.enums.ReservationStatus;
import hei.school.nmn.entity.enums.UserRole;
import hei.school.nmn.repository.MovieRepository;
import hei.school.nmn.repository.ProjectionRepository;
import hei.school.nmn.repository.ReservationRepository;
import hei.school.nmn.repository.RoomRepository;
import hei.school.nmn.repository.SeatRepository;
import hei.school.nmn.repository.UserRepository;
import hei.school.nmn.repository.model.JReservation;
import hei.school.nmn.service.MovieService;
import hei.school.nmn.service.ProjectionService;
import hei.school.nmn.service.ReservationService;
import hei.school.nmn.service.RoomService;
import hei.school.nmn.service.UserService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
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

class ReservationIT extends FacadeIT {

  @Autowired private UserService userService;
  @Autowired private MovieService movieService;
  @Autowired private RoomService roomService;
  @Autowired private ProjectionService projectionService;
  @Autowired private ReservationService reservationService;

  @Autowired private UserRepository userRepository;
  @Autowired private MovieRepository movieRepository;
  @Autowired private RoomRepository roomRepository;
  @Autowired private SeatRepository seatRepository;
  @Autowired private ProjectionRepository projectionRepository;
  @Autowired private ReservationRepository reservationRepository;

  @Autowired private TestRestTemplate restClient;
  @Autowired private JwtTestFactory jwtTestFactory;

  @BeforeEach
  void cleanDatabase() {
    reservationRepository.deleteAll();
    projectionRepository.deleteAll();
    seatRepository.deleteAll();
    roomRepository.deleteAll();
    movieRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void should_reserve_seats_for_a_projection() {
    UUID userId = registerUser("client@example.com", UserRole.CLIENT);
    UUID movieId = createMovie();
    Room room = createRoom("Room 1", 5);
    UUID projectionId = createProjection(movieId, room.id());

    List<UUID> seatIds = room.seats().stream().limit(2).map(Seat::id).toList();
    ReservationResponse reservation =
        reservationService.create(new ReservationRequest(projectionId, seatIds), userId);

    assertThat(reservation.id()).isNotNull();
    assertThat(reservation.user().id()).isEqualTo(userId);
    assertThat(reservation.projection().id()).isEqualTo(projectionId);
    assertThat(reservation.seats())
        .extracting(Seat::id)
        .containsExactlyInAnyOrderElementsOf(seatIds);

    JReservation saved = reservationRepository.findById(reservation.id()).orElseThrow();
    assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
    assertThat(saved.getReservedAt()).isNotNull();
  }

  @Test
  void should_reject_second_reservation_on_same_seat() {
    UUID firstUser = registerUser("client1@example.com", UserRole.CLIENT);
    UUID secondUser = registerUser("client2@example.com", UserRole.CLIENT);
    UUID movieId = createMovie();
    Room room = createRoom("Room 1", 2);
    UUID projectionId = createProjection(movieId, room.id());
    List<UUID> seatIds = room.seats().stream().limit(1).map(Seat::id).toList();

    reservationService.create(new ReservationRequest(projectionId, seatIds), firstUser);

    assertThrows(
        IllegalStateException.class,
        () -> reservationService.create(new ReservationRequest(projectionId, seatIds), secondUser));
  }

  @Test
  void should_confirm_then_cancel_reservation() {
    UUID userId = registerUser("client@example.com", UserRole.CLIENT);
    UUID movieId = createMovie();
    Room room = createRoom("Room 1", 3);
    UUID projectionId = createProjection(movieId, room.id());
    List<UUID> seatIds = room.seats().stream().limit(1).map(Seat::id).toList();

    ReservationResponse reservation =
        reservationService.create(new ReservationRequest(projectionId, seatIds), userId);
    assertThat(statusOf(reservation.id())).isEqualTo(ReservationStatus.PENDING);

    reservationService.confirm(reservation.id());
    assertThat(statusOf(reservation.id())).isEqualTo(ReservationStatus.CONFIRMED);

    reservationService.cancel(reservation.id());
    assertThat(statusOf(reservation.id())).isEqualTo(ReservationStatus.CANCELLED);
  }

  @Test
  void should_reject_seat_from_another_room() {
    UUID userId = registerUser("client@example.com", UserRole.CLIENT);
    UUID movieId = createMovie();
    Room room1 = createRoom("Room A", 2);
    Room room2 = createRoom("Room B", 2);
    UUID projectionId = createProjection(movieId, room1.id());
    UUID foreignSeatId = room2.seats().get(0).id();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            reservationService.create(
                new ReservationRequest(projectionId, List.of(foreignSeatId)), userId));
  }

  @Test
  void should_require_manager_to_update_movie() {
    UUID clientId = registerUser("client@example.com", UserRole.CLIENT);
    UUID managerId = registerUser("manager@example.com", UserRole.MANAGER);
    UUID movieId = createMovie();
    String clientToken = jwtTestFactory.tokenFor(clientId, UserRole.CLIENT);
    String managerToken = jwtTestFactory.tokenFor(managerId, UserRole.MANAGER);

    ResponseEntity<String> forbidden = putMovie(clientToken, movieId);
    assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    ResponseEntity<String> ok = putMovie(managerToken, movieId);
    assertThat(ok.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void should_create_reservation_via_http_with_principal_as_owner() {
    UUID userId = registerUser("client@example.com", UserRole.CLIENT);
    UUID movieId = createMovie();
    Room room = createRoom("Room 1", 5);
    UUID projectionId = createProjection(movieId, room.id());
    List<UUID> seatIds = room.seats().stream().limit(2).map(Seat::id).toList();
    String body =
        "{\"projectionId\":\""
            + projectionId
            + "\",\"seatIds\":[\""
            + seatIds.get(0)
            + "\",\""
            + seatIds.get(1)
            + "\"]}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(jwtTestFactory.tokenFor(userId, UserRole.CLIENT));

    ResponseEntity<String> response =
        restClient.exchange(
            "/api/reservations", HttpMethod.POST, new HttpEntity<>(body, headers), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains(userId.toString());
  }

  private ReservationStatus statusOf(UUID reservationId) {
    return reservationRepository.findById(reservationId).orElseThrow().getStatus();
  }

  private ResponseEntity<String> putMovie(String bearerToken, UUID movieId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(bearerToken);
    String body =
        "{"
            + "\"id\":\""
            + movieId
            + "\","
            + "\"title\":\"Inception\","
            + "\"genre\":\"ACTION\","
            + "\"description\":\"A thief steals secrets from dreams\","
            + "\"duration\":\"PT2H28M\""
            + "}";
    return restClient.exchange(
        "/api/movies/" + movieId, HttpMethod.PUT, new HttpEntity<>(body, headers), String.class);
  }

  private UUID registerUser(String email, UserRole role) {
    return userService
        .register(
            new UserRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), email, "password123", "0323456789", role))
        .id();
  }

  private UUID createMovie() {
    return movieService
        .create(
            Movie.builder()
                .title("Interstellar")
                .genre(Genre.SCI_FI)
                .description("A journey beyond the stars")
                .duration(Duration.ofHours(2).plusMinutes(49))
                .build())
        .id();
  }

  private Room createRoom(String number, int seatCount) {
    List<Seat> seats =
        IntStream.rangeClosed(1, seatCount)
            .mapToObj(i -> Seat.builder().number("A" + i).build())
            .toList();
    return roomService.create(
        Room.builder().number(number).capacity(seatCount).seats(seats).build());
  }

  private UUID createProjection(UUID movieId, UUID roomId) {
    return projectionService
        .create(
            Projection.builder()
                .datetime(Instant.now().plus(Duration.ofDays(1)))
                .seatPrice(new BigDecimal("12.50"))
                .movie(Movie.builder().id(movieId).build())
                .room(Room.builder().id(roomId).build())
                .build())
        .id();
  }
}
