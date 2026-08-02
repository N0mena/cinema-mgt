package hei.school.nmn.repository;

import hei.school.nmn.repository.model.JUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<JUser, UUID> {

}
