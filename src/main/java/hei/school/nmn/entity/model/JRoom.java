package hei.school.nmn.entity.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.expression.spel.ast.Projection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"seats", "projections"})
@EqualsAndHashCode(of = "id")
public class JRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JSeat> seats = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Projection> projections = new ArrayList<>();

    public void addSeat(JSeat seat) {
        seats.add(seat);
        seat.setRoom(this);
    }

    public void removeSeat(JSeat seat) {
        seats.remove(seat);
        seat.setRoom(null);
    }
}