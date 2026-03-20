package com.example.demo.repository;

import com.example.demo.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e WHERE " +
            " e.date BETWEEN :startDate AND :endDate" +
            " AND e.duration BETWEEN :durationMin AND :durationMax" +
            " AND e.maxPlaces BETWEEN :placesMin AND :placesMax" +
            " AND e.locationId = :locationId" +
            " AND e.status = :eventStatus" +
            " AND e.name = :eventName" +
            " AND e.cost BETWEEN :costMin AND :costMax")
    List<EventEntity> findEventsByFilterParams(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("durationMin") int durationMin,
            @Param("durationMax") int durationMax,
            @Param("placesMin") int placesMin,
            @Param("placesMax") int placesMax,
            @Param("locationId") int locationId,
            @Param("eventStatus") String eventStatus,
            @Param("eventName") String eventName,
            @Param("costMin") BigDecimal costMin,
            @Param("costMax") BigDecimal costMax);

    List<EventEntity> findByOwnerId(long ownerId);
}
