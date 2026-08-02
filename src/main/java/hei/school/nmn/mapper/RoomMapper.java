package hei.school.nmn.mapper;

import hei.school.nmn.entity.Room;
import hei.school.nmn.entity.model.JRoom;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

  public static JRoom toJ(Room room) {
    JRoom jRoom =
        JRoom.builder().id(room.id()).number(room.number()).capacity(room.capacity()).build();
    if (room.seats() != null) {
      jRoom.setSeats(
          room.seats().stream()
              .map(seat -> SeatMapper.toJ(seat, jRoom))
              .collect(Collectors.toList()));
    }
    return jRoom;
  }

  public static Room toDomain(JRoom room) {
    return Room.builder()
        .id(room.getId())
        .number(room.getNumber())
        .capacity(room.getCapacity())
        .seats(room.getSeats().stream().map(SeatMapper::toDomain).collect(Collectors.toList()))
        .build();
  }

  public static Room shallow(JRoom room) {
    return Room.builder()
        .id(room.getId())
        .number(room.getNumber())
        .capacity(room.getCapacity())
        .build();
  }
}
