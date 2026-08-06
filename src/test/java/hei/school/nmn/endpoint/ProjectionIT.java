package hei.school.nmn.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import hei.school.nmn.conf.FacadeIT;
import hei.school.nmn.conf.JwtTestFactory;
import hei.school.nmn.endpoint.dto.request.UserRequest;
import hei.school.nmn.entity.Movie;
import hei.school.nmn.entity.Projection;
import hei.school.nmn.entity.Room;
import hei.school.nmn.entity.Seat;
import hei.school.nmn.entity.enums.Genre;
import hei.school.nmn.entity.enums.UserRole;
import hei.school.nmn.repository.*;
import hei.school.nmn.service.MovieService;
import hei.school.nmn.service.ProjectionService;
import hei.school.nmn.service.RoomService;
import hei.school.nmn.service.UserService;
import hei.school.nmn.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class ProjectionIT extends FacadeIT {

    @Autowired private UserService userService;
    @Autowired private MovieService movieService;
    @Autowired private RoomService roomService;
    @Autowired private ProjectionService projectionService;

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
    void should_return_200_for_everyone_getting_projections() {
        UUID clientId = registerUser("client@example.com", UserRole.CLIENT);
        UUID employeeId = registerUser("employee@example.com", UserRole.EMPLOYEE);
        UUID managerId = registerUser("manager@example.com", UserRole.MANAGER);
        UUID movieId = createMovie();
        Room room = createRoom("Room 1", 3);
        createProjection(movieId, room.id());

        String clientToken = jwtTestFactory.tokenFor(clientId, UserRole.CLIENT);
        String employeeToken = jwtTestFactory.tokenFor(employeeId, UserRole.EMPLOYEE);
        String managerToken = jwtTestFactory.tokenFor(managerId, UserRole.MANAGER);

        assertThat(getProjections(clientToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getProjections(employeeToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getProjections(managerToken).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_reject_client_updating_projection() {
        UUID clientId = registerUser("client@example.com", UserRole.CLIENT);
        UUID movieId = createMovie();
        Room room = createRoom("Room 1", 3);
        UUID projectionId = createProjection(movieId, room.id());
        String clientToken = jwtTestFactory.tokenFor(clientId, UserRole.CLIENT);

        ResponseEntity<String> response = putProjection(clientToken, projectionId, movieId, room.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void should_reject_employee_updating_projection() {
        UUID employeeId = registerUser("employee@example.com", UserRole.EMPLOYEE);
        UUID movieId = createMovie();
        Room room = createRoom("Room 1", 3);
        UUID projectionId = createProjection(movieId, room.id());
        String employeeToken = jwtTestFactory.tokenFor(employeeId, UserRole.EMPLOYEE);

        ResponseEntity<String> response = putProjection(employeeToken, projectionId, movieId, room.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void should_allow_manager_updating_projection() {
        UUID managerId = registerUser("manager@example.com", UserRole.MANAGER);
        UUID movieId = createMovie();
        Room room = createRoom("Room 1", 3);
        UUID projectionId = createProjection(movieId, room.id());
        String managerToken = jwtTestFactory.tokenFor(managerId, UserRole.MANAGER);

        ResponseEntity<String> response = putProjection(managerToken, projectionId, movieId, room.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_read_and_delete_projection() {
        UUID movieId = createMovie();
        Room room = createRoom("Room 1", 2);
        UUID projectionId = createProjection(movieId, room.id());

        assertThat(projectionService.getById(projectionId).id()).isEqualTo(projectionId);
        assertThat(projectionService.getByMovie(movieId)).hasSize(1);
        assertThat(projectionService.getUpcoming()).isNotEmpty();

        projectionService.delete(projectionId);
        assertThrows(NotFoundException.class, () -> projectionService.getById(projectionId));
        assertThrows(NotFoundException.class, () -> projectionService.delete(projectionId));
    }

    @Test
    void should_throw_when_projection_references_missing_entities() {
        UUID movieId = createMovie();
        Room room = createRoom("Room 1", 2);

        assertThrows(
                NotFoundException.class,
                () ->
                        projectionService.create(
                                Projection.builder()
                                        .datetime(Instant.now().plus(Duration.ofDays(1)))
                                        .seatPrice(new BigDecimal("12.50"))
                                        .movie(Movie.builder().id(UUID.randomUUID()).build())
                                        .room(Room.builder().id(room.id()).build())
                                        .build()));

        assertThrows(
                NotFoundException.class,
                () ->
                        projectionService.create(
                                Projection.builder()
                                        .datetime(Instant.now().plus(Duration.ofDays(1)))
                                        .seatPrice(new BigDecimal("12.50"))
                                        .movie(Movie.builder().id(movieId).build())
                                        .room(Room.builder().id(UUID.randomUUID()).build())
                                        .build()));

        assertThrows(
                NotFoundException.class,
                () -> projectionService.getById(UUID.randomUUID()));
    }

    private ResponseEntity<String> getProjections(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        return restClient.exchange(
                "/api/projections", HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private ResponseEntity<String> putProjection(
            String bearerToken, UUID projectionId, UUID movieId, UUID roomId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body =
                "{"
                        + "\"datetime\":\""
                        + Instant.now().plus(Duration.ofDays(2))
                        + "\","
                        + "\"seatPrice\":\"15.00\","
                        + "\"movie\":{\"id\":\""
                        + movieId
                        + "\"},"
                        + "\"room\":{\"id\":\""
                        + roomId
                        + "\"}"
                        + "}";
        return restClient.exchange(
                "/api/projections/" + projectionId,
                HttpMethod.PUT,
                new HttpEntity<>(body, headers),
                String.class);
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