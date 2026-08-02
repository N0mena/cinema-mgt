package hei.school.nmn.repository;

import hei.school.nmn.entity.enums.ReservationStatus;
import hei.school.nmn.entity.model.JReservation;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<JReservation, UUID> {
  List<JReservation> findByUser_Id(UUID userId);

  List<JReservation> findByProjection_Id(UUID projectionId);

  boolean existsByProjection_IdAndStatusInAndSeats_IdIn(
      UUID projectionId, Collection<ReservationStatus> statuses, Collection<UUID> seatIds);
}
