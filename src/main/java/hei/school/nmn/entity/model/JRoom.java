package hei.school.nmn.entity.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
  @OneToMany(
      mappedBy = "room",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<JSeat> seats = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
  private List<JProjection> projections = new ArrayList<>();

  public void addSeat(JSeat seat) {
    seats.add(seat);
    seat.setRoom(this);
  }

  public void removeSeat(JSeat seat) {
    seats.remove(seat);
    seat.setRoom(null);
  }
}
