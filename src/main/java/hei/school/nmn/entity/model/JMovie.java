package hei.school.nmn.entity.model;

import hei.school.nmn.entity.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Data
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column
    private Genre genre;

    @Column(length = 2000)
    private String description;

    private Duration duration;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JProjection> projections = new ArrayList<>();
}