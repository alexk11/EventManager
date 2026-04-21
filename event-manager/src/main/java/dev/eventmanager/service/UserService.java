package dev.eventmanager.service;

import dev.eventmanager.converter.UserConverter;
import dev.eventmanager.entity.UserEntity;
import dev.eventmanager.exception.ServiceException;
import dev.eventmanager.model.JwtResponse;
import dev.eventmanager.model.Role;
import dev.eventmanager.model.UserCredentials;
import dev.eventmanager.model.dto.UserDto;
import dev.eventmanager.model.dto.UserRegistrationDto;
import dev.eventmanager.repository.UserRepository;
import dev.eventmanager.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomAuthenticationProvider authProvider;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(UserRegistrationDto dto) {
        log.info("Creating user with login '{}'", dto.getLogin());
        if (userRepository.existsByLogin(dto.getLogin())) {
            throw new IllegalArgumentException("Username already taken");
        }
        var userToSave = UserEntity.builder()
                .login(dto.getLogin())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .age(dto.getAge())
                .role(Role.USER)
                .build();
        UserEntity userEntity = userRepository.save(userToSave);
        return UserConverter.toDto(userEntity);
    }


    public UserDto getUser(long userId) {
        log.info("Getting user by the id = {}", userId);
        return userRepository
                .findById(userId)
                .map(UserConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));
    }


    public JwtResponse authUser(UserCredentials userCredentials) {
        log.info("Authenticating user '{}'", userCredentials.getLogin());
        return userRepository
                .findByLogin(userCredentials.getLogin())
                .map(user -> {
                    if (passwordEncoder.matches(
                            CharBuffer.wrap(userCredentials.getPassword()), user.getPasswordHash())) {
                        return new JwtResponse(authProvider.createToken(UserConverter.toDto(user)));
                    }
                    return null;
                })
                .orElseThrow(() -> new ServiceException(HttpStatus.UNAUTHORIZED.value(), "User not found or password is invalid"));
    }


    public UserDto findByLogin(String login) {
        log.info("Getting user with login '{}'", login);
        var dbUser = userRepository
                .findByLogin(login)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));
        return UserConverter.toDto(dbUser);
    }


    public boolean isUserExistsByLogin(String login) {
        log.info("Checking if user with login '{}' exists", login);
        return userRepository.existsByLogin(login);
    }


    public UserDto saveUser(UserDto userDto) {
        log.info("Saving user '{}'", userDto.getLogin());
        var savedUser = userRepository.save(UserConverter.toEntity(userDto));
        return UserConverter.toDto(savedUser);
    }

}
