package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.CreateUserRequestDto;
import com.anq.library_management_system.dto.UserResponseDto;
import com.anq.library_management_system.entity.Role;
import com.anq.library_management_system.entity.User;
import com.anq.library_management_system.exception.UserNotFoundException;
import com.anq.library_management_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponseDto toDto(User user) {

        UserResponseDto dto = new UserResponseDto();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return dto;
    }

    public List<UserResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserResponseDto> userDtos = new ArrayList<>();

        for (User user : users) {

            UserResponseDto userDto = toDto(user);

            userDtos.add(userDto);
        }

        return userDtos;
    }

    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + id + " not found"
                ));

        return toDto(user);
    }

    public UserResponseDto createUser(CreateUserRequestDto request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return toDto(savedUser);
    }

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id " + id + " not found"
                ));

        userRepository.delete(user);
    }
}