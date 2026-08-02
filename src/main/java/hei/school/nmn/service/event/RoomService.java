package hei.school.nmn.service.event;

import hei.school.nmn.entity.Room;
import hei.school.nmn.repository.RoomRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Room findById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Room not found with id: " + id));
    }

    @Transactional
    public Room create(Room room) {
        validate(room);
        if (roomRepository.existsByNumber(room.getNumber())) {
            throw new BadRequestException("A room with number '" + room.getNumber() + "' already exists.");
        }
        return roomRepository.save(room);
    }

    @Transactional
    public Room update(UUID id, Room updatedRoom) {
        Room existing = findById(id);
        validate(updatedRoom);

        roomRepository.findByNumber(updatedRoom.getNumber())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new BadRequestException("A room with number '" + updatedRoom.getNumber() + "' already exists.");
                });

        existing.setNumber(updatedRoom.getNumber());
        existing.setCapacity(updatedRoom.getCapacity());
        return roomRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        Room existing = findById(id);
        if (!existing.getProjections().isEmpty()) {
            throw new BadRequestException("Cannot delete a room that still has projections scheduled.");
        }
        roomRepository.delete(existing);
    }

    private void validate(Room room) {
        if (room.getNumber() == null || room.getNumber().isBlank()) {
            throw new BadRequestException("Room number is required.");
        }
        if (room.getCapacity() <= 0) {
            throw new BadRequestException("Room capacity must be greater than zero.");
        }
    }
}