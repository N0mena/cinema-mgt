package hei.school.nmn.repository;

import hei.school.nmn.entity.model.JRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<JRoom, UUID> {
}
