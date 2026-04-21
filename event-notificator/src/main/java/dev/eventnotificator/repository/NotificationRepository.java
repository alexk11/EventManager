package dev.eventnotificator.repository;

import dev.eventnotificator.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Notifications n SET n.isRead = true, n.readAt = NOW() WHERE " +
            "n.id IN (:ids) AND n.userId = :userId",
            nativeQuery = true)
    void markAsReadByIdsAndUserId(@Param("userId") long userId, @Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Notifications n WHERE " +
            "n.read_at < NOW() - make_interval(days => :days)",
            nativeQuery = true)
    void deleteReadNotificationsOlderDays(@Param("days") int days);
}
