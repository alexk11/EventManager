package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.dto.UserDto;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DefaultUserInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        createUserIfNotExists("admin", "admin", Role.ADMIN);
        createUserIfNotExists("user", "user", Role.USER);
    }

    private void createUserIfNotExists(String login, String password, Role role) {
        if (userService.isUserExistsByLogin(login)) {
            return;
        }
        var user = new UserDto(
                null,
                login,
                passwordEncoder.encode(password),
                30,
                role);
        userService.saveUser(user);
    }

}
