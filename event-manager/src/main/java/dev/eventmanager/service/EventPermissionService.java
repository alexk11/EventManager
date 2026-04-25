package dev.eventmanager.service;

import dev.eventmanager.model.Role;
import dev.eventmanager.model.dto.UserDto;
import org.springframework.stereotype.Service;


@Service
public class EventPermissionService {

    public boolean canModify(UserDto currentUser, Long eventOwnerId) {

        return eventOwnerId.equals(currentUser.getId())
                || currentUser.getRole().equals(Role.ADMIN.name());
    }

}
