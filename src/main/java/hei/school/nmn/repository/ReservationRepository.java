package hei.school.nmn.repository;

import hei.school.nmn.entity.model.JReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<JReservation, UUID> {
}
