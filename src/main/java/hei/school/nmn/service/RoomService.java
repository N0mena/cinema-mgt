package hei.school.nmn.service;

import hei.school.nmn.entity.Room;
import hei.school.nmn.repository.model.JRoom;
import hei.school.nmn.mapper.RoomMapper;
import hei.school.nmn.mapper.SeatMapper;
import hei.school.nmn.repository.RoomRepository;
import hei.school.nmn.service.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  @Transactional(readOnly = true)
  public List<Room> getAll() {
    return roomRepository.findAll().stream().map(RoomMapper::toDomain).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Room getById(UUID id) {
    return roomRepository
        .findById(id)
        .map(RoomMapper::toDomain)
        .orElseThrow(() -> new NotFoundException("Room not found: " + id));
  }

  @Transactional
  public Room create(Room room) {
    return RoomMapper.toDomain(roomRepository.save(RoomMapper.toJ(room)));
  }

  @Transactional
  public Room update(UUID id, Room room) {
    JRoom existing = getJRoom(id);
    existing.setNumber(room.number());
    existing.setCapacity(room.capacity());
    if (room.seats() != null) {
      existing.getSeats().clear();
      room.seats().forEach(seat -> existing.addSeat(SeatMapper.toJ(seat, existing)));
    }
    return RoomMapper.toDomain(existing);
  }

  @Transactional
  public void delete(UUID id) {
    if (!roomRepository.existsById(id)) {
      throw new NotFoundException("Room not found: " + id);
    }
    roomRepository.deleteById(id);
  }

  private JRoom getJRoom(UUID id) {
    return roomRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Room not found: " + id));
  }
}
