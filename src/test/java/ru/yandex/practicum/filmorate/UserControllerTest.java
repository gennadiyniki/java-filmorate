package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.LocalDate;
import java.util.List;

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
    void createValidUser() {
        User createdUser = userController.createUser(validUser);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("test@email.com", createdUser.getEmail());
        assertEquals("testlogin", createdUser.getLogin());
    }

    @Test
    void useLoginWhenNameIsEmpty() {
        validUser.setName("");
        User createdUser = userController.createUser(validUser);

        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void useLoginWhenNameIsNull() {
        validUser.setName(null);
        User createdUser = userController.createUser(validUser);

        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void nullBirthday() {
        validUser.setBirthday(null);
        assertDoesNotThrow(() -> userController.createUser(validUser));
    }

    @Test
    void throwExceptionWhenEmailIsEmpty() {
        validUser.setEmail("");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void throwExceptionWhenEmailIsNull() {
        validUser.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void throwExceptionWhenEmailWithoutAt() {
        validUser.setEmail("invalid-email");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void throwExceptionWhenLoginIsEmpty() {
        validUser.setLogin("");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void throwExceptionWhenLoginIsNull() {
        validUser.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }

    @Test
    void throwExceptionWhenLoginContainsSpaces() {
        validUser.setLogin("login with spaces");
        assertThrows(ValidationException.class, () -> userController.createUser(validUser));
    }


    @Test
    void throwExceptionWhenDuplicateEmail() {
        userController.createUser(validUser); // Создаем первого пользователя

        User newUser = new User();
        newUser.setEmail("test@email.com"); // Тот же email
        newUser.setLogin("newlogin");
        newUser.setBirthday(LocalDate.of(1995, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(newUser));
    }

    @Test
    void updateUser() {
        User createdUser = userController.createUser(validUser);
        Long userId = createdUser.getId();

        User updateData = new User();
        updateData.setId(createdUser.getId());
        updateData.setEmail("new@email.com");
        updateData.setLogin("newlogin");
        updateData.setName("New Name");
        updateData.setBirthday(LocalDate.of(1995, 1, 1));

        User updatedUser = userController.updateUser(updateData);

        assertEquals("new@email.com", updatedUser.getEmail());
        assertEquals("newlogin", updatedUser.getLogin());
        assertEquals("New Name", updatedUser.getName());
    }

    @Test
    void partUpdateUser() {
        User createdUser = userController.createUser(validUser);
        Long userId = createdUser.getId();

        User updateData = new User();
        updateData.setId(createdUser.getId());
        updateData.setEmail("part@email.com");
        updateData.setLogin("testlogin"); // ← Добавить логин
        updateData.setName("Test User");

        User updatedUser = userController.updateUser(updateData);

        assertEquals("part@email.com", updatedUser.getEmail());
        assertEquals("testlogin", updatedUser.getLogin());
        assertEquals("Test User", updatedUser.getName());
    }

    @Test
    void getAllUsers() {
        userController.createUser(validUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@email.com");
        anotherUser.setLogin("anotherlogin");
        anotherUser.setBirthday(LocalDate.of(1985, 1, 1));
        userController.createUser(anotherUser);

        List<User> users = userController.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void returnEmptyListWhenNoUsers() {
        List<User> users = userController.getUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}