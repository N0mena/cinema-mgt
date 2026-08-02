package hei.school.nmn.entity.model;

import hei.school.nmn.entity.User;
import hei.school.nmn.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Table(name = "reservation")
@AllArgsConstructor
@Builder
@Getter
@Setter
public class JReservation {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private JUser user;

    @Column
    private JProjection projection;

    @Column
    private ReservationStatus status;

    @Column
    @ManyToMany
    private List<JSeat> seat;

    @Column
    private LocalDateTime reservedAt;
}
