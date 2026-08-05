package hei.school.nmn.mapper;

import hei.school.nmn.entity.Projection;
import hei.school.nmn.repository.model.JMovie;
import hei.school.nmn.repository.model.JProjection;
import hei.school.nmn.repository.model.JRoom;
import org.springframework.stereotype.Component;

@Component
public class ProjectionMapper {

  public static JProjection toJ(Projection projection, JMovie movie, JRoom room) {
    return JProjection.builder()
        .id(projection.id())
        .datetime(projection.datetime())
        .seatPrice(projection.seatPrice())
        .movie(movie)
        .room(room)
        .build();
  }

  public static Projection toDomain(JProjection projection) {
    return Projection.builder()
        .id(projection.getId())
        .datetime(projection.getDatetime())
        .seatPrice(projection.getSeatPrice())
        .movie(MovieMapper.shallow(projection.getMovie()))
        .room(RoomMapper.toDomain(projection.getRoom()))
        .build();
  }
}
