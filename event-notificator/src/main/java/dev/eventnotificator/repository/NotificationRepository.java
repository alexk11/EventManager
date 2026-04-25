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

    @Query(value = "SELECT n.id FROM NotificationEntity n WHERE " +
            "n.userId = :userId AND n.isRead = false")
    List<Long> findByUserIdAndIsReadFalse(@Param("userId") Long userId);

    @Query(value = "SELECT count(n) FROM NotificationEntity n WHERE " +
            "n.userId = :userId AND n.isRead = false")
    Long countByUserIdAndIsReadFalse(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Notifications SET is_read = true, read_at = NOW() WHERE " +
            "id IN (:ids) AND user_id = :userId",
            nativeQuery = true)
    void markAsReadByIdsAndUserId(@Param("userId") long userId, @Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Notifications n WHERE " +
            "n.read_at < NOW() - make_interval(days => :days) AND n.is_read = true",
            nativeQuery = true)
    void deleteReadNotificationsOlderDays(@Param("days") int days);
}
