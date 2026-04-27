package dev.eventnotificator.service;

import dev.eventnotificator.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationCounterService {

    private final StringRedisTemplate redisTemplate;
    private final NotificationRepository notificationRepository;

    public void incrementUnread(Long userId, long delta) {
        redisTemplate.opsForValue().increment(
                unreadKey(userId),
                delta
        );
    }

    public void syncUnreadFromDatabase(Long userId) {
        long unreadCount = notificationRepository
                .countByUserIdAndIsReadFalse(userId);
        redisTemplate.opsForValue().set(
                unreadKey(userId),
                Long.toString(unreadCount)
        );
    }

    private String unreadKey(Long userId) {
        return "notif:unread:" + userId;
    }

}
