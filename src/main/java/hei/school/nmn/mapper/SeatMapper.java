package hei.school.nmn.mapper;

import hei.school.nmn.entity.Seat;
import hei.school.nmn.repository.model.JRoom;
import hei.school.nmn.repository.model.JSeat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

  public static JSeat toJ(Seat seat, JRoom room) {
    return JSeat.builder().id(seat.id()).number(seat.number()).room(room).build();
  }

  public static Seat toDomain(JSeat seat) {
    return Seat.builder()
        .id(seat.getId())
        .number(seat.getNumber())
        .room(RoomMapper.shallow(seat.getRoom()))
        .build();
  }
}
