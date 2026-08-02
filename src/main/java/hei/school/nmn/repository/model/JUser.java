package hei.school.nmn.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import javax.annotation.Nullable;
import java.util.UUID;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "\"user\"")
public class JUser {
    @Id @GeneratedValue private UUID id;
    @Column
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String email;



}
