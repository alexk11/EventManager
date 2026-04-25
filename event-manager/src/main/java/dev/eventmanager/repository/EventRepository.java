package dev.eventmanager.repository;

import dev.eventmanager.entity.EventEntity;
import dev.eventmanager.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("""
            SELECT e FROM EventEntity e WHERE
             (:name IS NULL OR e.name LIKE %:name%) AND
             (:placesMin IS NULL OR e.maxPlaces >= :placesMin) AND
             (:placesMax IS NULL OR e.maxPlaces <= :placesMax) AND
             (CAST(:dateStartAfter as date) IS NULL OR e.date >= :dateStartAfter) AND
             (CAST(:dateStartBefore as date) IS NULL OR e.date <= :dateStartBefore) AND
             (:costMin IS NULL OR e.cost >= :costMin) AND
             (:costMax IS NULL OR e.cost <= :costMax) AND
             (:durationMin IS NULL OR e.duration >= :durationMin) AND
             (:durationMax IS NULL OR e.duration <= :durationMax) AND
             (:locationId IS NULL OR e.locationId = :locationId) AND
             (:status IS NULL OR e.status = :status)
            """)
    List<EventEntity> findEventsByFilterParams(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") BigDecimal costMin,
            @Param("costMax") BigDecimal costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Integer locationId,
            @Param("status") EventStatus status);

    List<EventEntity> findByOwnerId(Long ownerId);

    @Query(value = "SELECT e.id FROM Events e" +
                   " WHERE e.status = :status" +
                   " AND e.date <= NOW()" +
                   " AND e.date + INTERVAL '1 minute' * e.duration > NOW()",
            nativeQuery = true)
    List<Long> findStartedEventsWithStatus(
            @Param("status") EventStatus status);


    @Query(value = "SELECT e.id FROM Events e" +
                   " WHERE e.status = :status" +
                   " AND e.date + INTERVAL '1 minute' * e.duration < NOW()",
            nativeQuery = true)
    List<Long> findFinishedEventsWithStatus(
            @Param("status") EventStatus eventStatus);


    @Modifying
    @Transactional
    @Query("UPDATE EventEntity e SET e.status = :status WHERE e.id = :id")
    void changeStatus(
            @Param("id") Long id,
            @Param("status") EventStatus status);

    @Query(value = """
            SELECT count(*) > 0 FROM Events WHERE location_id = :locationId
             AND status != :excludeStatus
             AND date < :end
             AND date + (duration * INTERVAL '1 minute') > :start
            """, nativeQuery = true)
    boolean isTimeslotBusy(
            @Param("locationId") Long locationId,
            @Param("excludeStatus") EventStatus excludeStatus,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
