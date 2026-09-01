package com.anq.library_management_system.service;

import com.anq.library_management_system.dto.CreateUserRequestDto;
import com.anq.library_management_system.dto.UserResponseDto;
import com.anq.library_management_system.entity.Role;
import com.anq.library_management_system.entity.User;
import com.anq.library_management_system.exception.UserNotFoundException;
import com.anq.library_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {

        userService = new UserService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "testuser",
                        "test@gmail.com",
                        "123456"
                );

        String encodedPassword = "$2a$encodedPassword";

        when(passwordEncoder.encode("123456"))
                .thenReturn(encodedPassword);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {

                    User user = invocation.getArgument(0);
                    user.setId(1L);

                    return user;
                });

        UserResponseDto result =
                userService.createUser(request);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals(
                        "testuser",
                        result.getUsername()
                ),
                () -> assertEquals(
                        "test@gmail.com",
                        result.getEmail()
                ),
                () -> assertEquals(
                        Role.USER,
                        result.getRole()
                )
        );

        verify(passwordEncoder)
                .encode("123456");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void createUser_shouldAssignUserRoleAutomatically() {

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "reader",
                        "reader@gmail.com",
                        "password"
                );

        when(passwordEncoder.encode("password"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        UserResponseDto result =
                userService.createUser(request);

        assertEquals(
                Role.USER,
                result.getRole()
        );
    }

    @Test
    void createUser_shouldEncodePasswordBeforeSaving() {

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "testuser",
                        "test@gmail.com",
                        "123456"
                );

        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode("123456"))
                .thenReturn(encodedPassword);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                encodedPassword,
                savedUser.getPassword()
        );

        assertNotEquals(
                "123456",
                savedUser.getPassword()
        );
    }

    @Test
    void createUser_shouldSaveCorrectUserData() {

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "anna",
                        "anna@gmail.com",
                        "secret"
                );

        when(passwordEncoder.encode("secret"))
                .thenReturn("encoded-secret");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        "anna",
                        savedUser.getUsername()
                ),
                () -> assertEquals(
                        "anna@gmail.com",
                        savedUser.getEmail()
                ),
                () -> assertEquals(
                        "encoded-secret",
                        savedUser.getPassword()
                ),
                () -> assertEquals(
                        Role.USER,
                        savedUser.getRole()
                )
        );
    }

    @Test
    void getUserById_shouldReturnUser() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserResponseDto result =
                userService.getUserById(userId);

        assertAll(
                () -> assertEquals(
                        userId,
                        result.getId()
                ),
                () -> assertEquals(
                        "testuser",
                        result.getUsername()
                ),
                () -> assertEquals(
                        "test@gmail.com",
                        result.getEmail()
                ),
                () -> assertEquals(
                        Role.USER,
                        result.getRole()
                )
        );

        verify(userRepository)
                .findById(userId);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {

        Long userId = 100L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> userService.getUserById(userId)
                );

        assertEquals(
                "User with id " + userId + " not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        User firstUser = new User();
        firstUser.setId(1L);
        firstUser.setUsername("user1");
        firstUser.setEmail("user1@gmail.com");
        firstUser.setPassword("password1");
        firstUser.setRole(Role.USER);

        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUsername("librarian");
        secondUser.setEmail("librarian@gmail.com");
        secondUser.setPassword("password2");
        secondUser.setRole(Role.LIBRARIAN);

        when(userRepository.findAll())
                .thenReturn(
                        List.of(
                                firstUser,
                                secondUser
                        )
                );

        List<UserResponseDto> result =
                userService.getAllUsers();

        assertEquals(2, result.size());

        assertAll(
                () -> assertEquals(
                        "user1",
                        result.get(0).getUsername()
                ),
                () -> assertEquals(
                        Role.USER,
                        result.get(0).getRole()
                ),
                () -> assertEquals(
                        "librarian",
                        result.get(1).getUsername()
                ),
                () -> assertEquals(
                        Role.LIBRARIAN,
                        result.get(1).getRole()
                )
        );

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsersExist() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponseDto> result =
                userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .delete(user);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {

        Long userId = 100L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception =
                assertThrows(
                        UserNotFoundException.class,
                        () -> userService.deleteUser(userId)
                );

        assertEquals(
                "User with id " + userId + " not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .delete(any(User.class));
    }
}