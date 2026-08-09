package hei.school.nmn.repository.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "receipt")
public class JReceipt {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id", nullable = false)
  private JReservation reservation;

  private String filePath; // ex: /storage/receipts/{id}.pdf

  private LocalDateTime createdAt;
  private LocalDateTime completedAt;
}
