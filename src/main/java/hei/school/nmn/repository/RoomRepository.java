package hei.school.nmn.repository;

import hei.school.nmn.entity.model.JRoom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<JRoom, UUID> {}
