package hei.school.nmn.repository;

import hei.school.nmn.entity.model.JUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<JUser, UUID> {
    Optional<JUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
