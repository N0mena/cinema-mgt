package hei.school.nmn.entity.model;

import hei.school.nmn.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDate birthdate;
    @Column(nullable = false)
    private String email;
    @Column
    private String password;
    @Column
    private String phone;
    @Column
    private UserRole role;




}
