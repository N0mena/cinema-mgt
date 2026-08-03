package hei.school.nmn.service;

import hei.school.nmn.entity.Seat;
import hei.school.nmn.mapper.SeatMapper;
import hei.school.nmn.repository.RoomRepository;
import hei.school.nmn.repository.SeatRepository;
import hei.school.nmn.repository.model.JRoom;
import hei.school.nmn.repository.model.JSeat;
import hei.school.nmn.service.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SeatService {

  private final SeatRepository seatRepository;
  private final RoomRepository roomRepository;

  @Transactional(readOnly = true)
  public List<Seat> getAll() {
    return seatRepository.findAll().stream().map(SeatMapper::toDomain).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Seat getById(UUID id) {
    return seatRepository
        .findById(id)
        .map(SeatMapper::toDomain)
        .orElseThrow(() -> new NotFoundException("Seat not found: " + id));
  }

  @Transactional(readOnly = true)
  public List<Seat> getByRoom(UUID roomId) {
    return seatRepository.findByRoom_Id(roomId).stream()
        .map(SeatMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional
  public Seat create(Seat seat) {
    if (seat.room() == null) {
      throw new IllegalArgumentException("Seat must belong to a room");
    }
    JRoom room =
        roomRepository
            .findById(seat.room().id())
            .orElseThrow(() -> new NotFoundException("Room not found: " + seat.room().id()));
    JSeat saved = seatRepository.save(SeatMapper.toJ(seat, room));
    return SeatMapper.toDomain(saved);
  }

  @Transactional
  public void delete(UUID id) {
    if (!seatRepository.existsById(id)) {
      throw new NotFoundException("Seat not found: " + id);
    }
    seatRepository.deleteById(id);
  }
}
