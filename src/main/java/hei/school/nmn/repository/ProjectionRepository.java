package hei.school.nmn.repository;

import hei.school.nmn.entity.Projection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProjectionRepository extends JpaRepository<Projection, UUID> {
    List<Projection> findByMovieId(UUID movieId);
    List<Projection> findByRoomId(UUID roomId);
    List<Projection> findByRoomIdAndDatetimeBetween(UUID roomId, Instant from, Instant to);
}
