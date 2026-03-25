package dev.eventmanager.service;

import dev.eventmanager.entity.UserEntity;
import dev.eventmanager.exception.ServiceException;
import dev.eventmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByLogin(userName)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));

        return User.withUsername(userName)
                .password(userEntity.getPasswordHash())
                .authorities(userEntity.getRole())
                .build();
    }

}
