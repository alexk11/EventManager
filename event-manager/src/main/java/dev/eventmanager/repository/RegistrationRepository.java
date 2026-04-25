package dev.eventmanager.repository;

import dev.eventmanager.entity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    List<RegistrationEntity> findByUserId(Long userId);

    Optional<RegistrationEntity> findByUserIdAndEventId(Long userId, Long eventId);
}
