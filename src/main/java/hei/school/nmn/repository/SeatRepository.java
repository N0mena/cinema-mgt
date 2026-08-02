package hei.school.nmn.repository;

import hei.school.nmn.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByRoomId(UUID roomId);
    Optional<Seat> findByRoomIdAndNumber(UUID roomId, String number);
    boolean existsByRoomIdAndNumber(UUID roomId, String number);
}
