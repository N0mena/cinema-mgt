package hei.school.nmn.repository;

import hei.school.nmn.entity.model.JMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<JMovie, UUID> {
}
