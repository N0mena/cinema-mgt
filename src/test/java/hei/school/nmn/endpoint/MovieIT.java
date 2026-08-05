package hei.school.nmn.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import hei.school.nmn.conf.FacadeIT;
import hei.school.nmn.conf.JwtTestFactory;
import hei.school.nmn.endpoint.dto.request.UserRequest;
import hei.school.nmn.entity.Movie;
import hei.school.nmn.entity.enums.Genre;
import hei.school.nmn.entity.enums.UserRole;
import hei.school.nmn.repository.MovieRepository;
import hei.school.nmn.repository.UserRepository;
import hei.school.nmn.service.MovieService;
import hei.school.nmn.service.UserService;
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

class MovieIT extends FacadeIT {

    @Autowired private UserService userService;
    @Autowired private MovieService movieService;

    @Autowired private UserRepository userRepository;
    @Autowired private MovieRepository movieRepository;

    @Autowired private TestRestTemplate restClient;
    @Autowired private JwtTestFactory jwtTestFactory;

    @BeforeEach
    void cleanDatabase() {
        movieRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void should_return_200_for_everyone_getting_movies() {
        UUID clientId = registerUser("client@example.com", UserRole.CLIENT);
        createMovie();
        String clientToken = jwtTestFactory.tokenFor(clientId, UserRole.CLIENT);

        ResponseEntity<String> response = getMovies(clientToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_reject_client_updating_movie() {
        UUID clientId = registerUser("client@example.com", UserRole.CLIENT);
        UUID movieId = createMovie();
        String clientToken = jwtTestFactory.tokenFor(clientId, UserRole.CLIENT);

        ResponseEntity<String> response = putMovie(clientToken, movieId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void should_reject_employee_updating_movie() {
        UUID employeeId = registerUser("employee@example.com", UserRole.EMPLOYEE);
        UUID movieId = createMovie();
        String employeeToken = jwtTestFactory.tokenFor(employeeId, UserRole.EMPLOYEE);

        ResponseEntity<String> response = putMovie(employeeToken, movieId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void should_allow_manager_updating_movie() {
        UUID managerId = registerUser("manager@example.com", UserRole.MANAGER);
        UUID movieId = createMovie();
        String managerToken = jwtTestFactory.tokenFor(managerId, UserRole.MANAGER);

        ResponseEntity<String> response = putMovie(managerToken, movieId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<String> getMovies(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        return restClient.exchange(
                "/api/movies", HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private ResponseEntity<String> putMovie(String bearerToken, UUID movieId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body =
                "{"
                        + "\"id\":\""
                        + movieId
                        + "\","
                        + "\"title\":\"Inception\","
                        + "\"genre\":[\"ACTION\"],"
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
}