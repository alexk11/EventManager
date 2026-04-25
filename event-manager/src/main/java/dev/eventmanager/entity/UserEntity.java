package dev.eventmanager.entity;

import dev.eventmanager.model.Role;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@Entity
@Table(name = "users")
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Role role;
}
