package com.example.demo.service;

import com.example.demo.converter.UserConverter;
import com.example.demo.entity.User;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.UserCredentials;
import com.example.demo.model.dto.UserDto;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create new user
     * @param userDto
     * @return
     */
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user with the login '{}'", userDto.getLogin());
        User userEntity = userRepository.save(UserConverter.toEntity(userDto));
        return UserConverter.toDto(userEntity);
    }

    /**
     * Get user data
     * @param userId
     * @return
     */
    public UserDto getUser(long userId) {
        log.info("Getting user by the id = {}", userId);
        return userRepository
                .findById(userId)
                .map(UserConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Пользователь не найден"));
    }

    /**
     * Authenticate user and return a JWT token
     * @param userCredentials
     * @return
     */
    public String authUser(UserCredentials userCredentials) {
        log.info("Authenticating user '{}'", userCredentials.getLogin());
        if (userRepository.findByLogin(userCredentials.getLogin()).isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Пользователь не найден");
        }
        return "JWT token";
    }
}
