package dev.eventmanager.service;

import org.springframework.stereotype.Service;
import dev.eventmanager.model.dto.UserDto;
import dev.eventmanager.model.Role;
import dev.eventmanager.model.dto.event.EventDto;


@Service
public class EventPermissionService {

    public boolean canModify(
            UserDto currentUser,
            Long eventOwnerId) {
        return eventOwnerId.equals(currentUser.getId())
                || currentUser.getRole().equals(Role.ADMIN.name());
    }
}
