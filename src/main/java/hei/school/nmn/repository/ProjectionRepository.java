package hei.school.nmn.repository;

import hei.school.nmn.entity.model.JProjection;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectionRepository extends JpaRepository<JProjection, UUID> {
  List<JProjection> findByMovie_Id(UUID movieId);

  List<JProjection> findByDatetimeAfter(Instant datetime);
}
