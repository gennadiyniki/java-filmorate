package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private UserController userController;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setEmail("test@email.com");
        validUser.setLogin("testlogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));

    }
    @Test
    void CreateValidUser() {
        User createdUser = userController.createUser(validUser);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("test@email.com", createdUser.getEmail());
        assertEquals("testlogin", createdUser.getLogin());
    }

    @Test
    void UseLoginWhenNameIsEmpty() {
        validUser.setName("");
        User createdUser = userController.createUser(validUser);

        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void UseLoginWhenNameIsNull() {
        validUser.setName(null);
        User createdUser = userController.createUser(validUser);

        assertEquals("testlogin", createdUser.getName());
    }
    @Test
    void NullBirthday() {
        validUser.setBirthday(null);
        assertDoesNotThrow(() -> userController.createUser(validUser));
    }

    @Test
    void ThrowExceptionWhenEmailIsEmpty() {
        validUser.setEmail("");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void ThrowExceptionWhenEmailIsNull() {
        validUser.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void ThrowExceptionWhenEmailWithoutAt() {
        validUser.setEmail("invalid-email");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void ThrowExceptionWhenLoginIsEmpty() {
        validUser.setLogin("");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void ThrowExceptionWhenLoginIsNull() {
        validUser.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void ThrowExceptionWhenLoginContainsSpaces() {
        validUser.setLogin("login with spaces");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }


    @Test
    void ThrowExceptionWhenDuplicateEmail() {
        userController.createUser(validUser); // Создаем первого пользователя

        User newUser = new User();
        newUser.setEmail("test@email.com"); // Тот же email
        newUser.setLogin("newlogin");
        newUser.setBirthday(LocalDate.of(1995, 1, 1));

        assertThrows(DuplicatedDataException.class, () -> userController.createUser(newUser));
    }

    @Test
    void UpdateUser() {
        User createdUser = userController.createUser(validUser);
        Long userId = createdUser.getId();

        User updateData = new User();
        updateData.setEmail("new@email.com");
        updateData.setLogin("newlogin");
        updateData.setName("New Name");
        updateData.setBirthday(LocalDate.of(1995, 1, 1));

        User updatedUser = userController.updateUser(updateData, userId);

        assertEquals("new@email.com", updatedUser.getEmail());
        assertEquals("newlogin", updatedUser.getLogin());
        assertEquals("New Name", updatedUser.getName());
    }

    @Test
    void PartUpdateUser() {
        User createdUser = userController.createUser(validUser);
        Long userId = createdUser.getId();

        User updateData = new User();
        updateData.setEmail("partial@email.com");

        User updatedUser = userController.updateUser(updateData, userId);

        assertEquals("partial@email.com", updatedUser.getEmail());
        assertEquals("testlogin", updatedUser.getLogin());
        assertEquals("Test User", updatedUser.getName());
    }

    @Test
    void GetAllUsers() {
        userController.createUser(validUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherlogin");
        anotherUser.setBirthday(LocalDate.of(1985, 1, 1));
        userController.createUser(anotherUser);

        ArrayList<User> users = userController.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() {
        ArrayList<User> users = userController.getUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}