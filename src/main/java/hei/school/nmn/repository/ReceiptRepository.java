package hei.school.nmn.repository;

import hei.school.nmn.repository.model.JReceipt;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<JReceipt, UUID> {}
