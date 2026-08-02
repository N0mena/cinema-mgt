package hei.school.nmn.repository;

import hei.school.nmn.entity.Movie;
import hei.school.nmn.entity.enums.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    List<Movie> findByGenreContaining(Genre genre);
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
