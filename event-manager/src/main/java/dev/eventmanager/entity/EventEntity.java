package dev.eventmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.eventmanager.model.EventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@Table(name = "events")
@AllArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Integer maxPlaces;

    @Column(nullable = false)
    private Integer occupiedPlaces;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private EventStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationEntity> registrations;
}
