package hei.school.nmn.service;

import hei.school.nmn.endpoint.dto.request.ReservationRequest;
import hei.school.nmn.endpoint.dto.response.ReservationResponse;
import hei.school.nmn.entity.enums.ReservationStatus;
import hei.school.nmn.entity.model.JProjection;
import hei.school.nmn.entity.model.JReservation;
import hei.school.nmn.entity.model.JSeat;
import hei.school.nmn.entity.model.JUser;
import hei.school.nmn.mapper.ReservationMapper;
import hei.school.nmn.repository.ProjectionRepository;
import hei.school.nmn.repository.ReservationRepository;
import hei.school.nmn.repository.SeatRepository;
import hei.school.nmn.repository.UserRepository;
import hei.school.nmn.service.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReservationService {

  private static final List<ReservationStatus> ACTIVE_STATUSES =
      List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final ProjectionRepository projectionRepository;
  private final SeatRepository seatRepository;

  @Transactional
  public ReservationResponse create(ReservationRequest request, UUID userId) {
    JUser user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    JProjection projection =
        projectionRepository
            .findById(request.projectionId())
            .orElseThrow(
                () -> new NotFoundException("Projection not found: " + request.projectionId()));

    List<JSeat> seats = seatRepository.findAllById(request.seatIds());
    if (seats.size() != request.seatIds().size()) {
      throw new IllegalArgumentException("Some requested seats do not exist or are duplicated");
    }
    UUID roomId = projection.getRoom().getId();
    for (JSeat seat : seats) {
      if (!roomId.equals(seat.getRoom().getId())) {
        throw new IllegalArgumentException(
            "Seat " + seat.getNumber() + " does not belong to the projection room");
      }
    }
    if (reservationRepository.existsByProjection_IdAndStatusInAndSeats_IdIn(
        projection.getId(), ACTIVE_STATUSES, request.seatIds())) {
      throw new IllegalStateException("One or more seats are already reserved for this projection");
    }

    JReservation saved = reservationRepository.save(ReservationMapper.toJ(user, projection, seats));
    return ReservationMapper.toResponse(ReservationMapper.toDomain(saved));
  }

  @Transactional(readOnly = true)
  public ReservationResponse getById(UUID id) {
    return ReservationMapper.toResponse(ReservationMapper.toDomain(getEntity(id)));
  }

  @Transactional(readOnly = true)
  public List<ReservationResponse> getByUser(UUID userId) {
    return reservationRepository.findByUser_Id(userId).stream()
        .map(ReservationMapper::toDomain)
        .map(ReservationMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ReservationResponse> getByProjection(UUID projectionId) {
    return reservationRepository.findByProjection_Id(projectionId).stream()
        .map(ReservationMapper::toDomain)
        .map(ReservationMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<ReservationResponse> getAll() {
    return reservationRepository.findAll().stream()
        .map(ReservationMapper::toDomain)
        .map(ReservationMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public ReservationResponse cancel(UUID id) {
    JReservation reservation = getEntity(id);
    reservation.setStatus(ReservationStatus.CANCELLED);
    return ReservationMapper.toResponse(ReservationMapper.toDomain(reservation));
  }

  @Transactional
  public ReservationResponse confirm(UUID id) {
    JReservation reservation = getEntity(id);
    reservation.setStatus(ReservationStatus.CONFIRMED);
    return ReservationMapper.toResponse(ReservationMapper.toDomain(reservation));
  }

  private JReservation getEntity(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
  }
}
