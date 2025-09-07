package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserValidator userValidator;
    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);

        userValidator.validateEmail(user.getEmail());
        userValidator.validateLogin(user.getLogin());
        userValidator.validateBirthday(user.getBirthday());

        for (User saveUser : users.values()) {
            if (saveUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                log.warn("Email уже используется: {}", user.getEmail());
                throw new ValidationException("Этот имейл уже используется");
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя установлено равным логину: {}", user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан успешно с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        log.info("Запрос на обновление пользователя: {}", updatedUser);

        if (updatedUser.getId() == 0) {
            log.warn("Попытка обновления пользователя без ID");
            throw new ValidationException("ID пользователя обязателен для обновления");
        }

        if (!users.containsKey(updatedUser.getId())) {
            log.warn("Пользователь с ID {} не найден", updatedUser.getId());
            throw new ValidationException("Пользователь с ID " + updatedUser.getId() + " не найден");
        }

        User saveUser = users.get(updatedUser.getId());

        if (updatedUser.getEmail() != null) {
            userValidator.validateEmail(updatedUser.getEmail());

            if (!updatedUser.getEmail().equals(saveUser.getEmail())) {
                for (User userInMap : users.values()) {
                    if (userInMap.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
                        log.warn("Email уже используется: {}", updatedUser.getEmail());
                        throw new ValidationException("Этот имейл уже используется");
                    }
                }
            }
            saveUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getLogin() != null) {
            userValidator.validateLogin(updatedUser.getLogin());
            saveUser.setLogin(updatedUser.getLogin());
        }

        if (updatedUser.getName() != null) {
            if (updatedUser.getName().isBlank()) {
                saveUser.setName(saveUser.getLogin());
            } else {
                saveUser.setName(updatedUser.getName());
            }
        }

        if (updatedUser.getBirthday() != null) {
            userValidator.validateBirthday(updatedUser.getBirthday());
            saveUser.setBirthday(updatedUser.getBirthday());
        }

        log.info("Пользователь с ID {} успешно обновлен", updatedUser.getId());
        return saveUser;
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с ID: {}", userId);
        User user = users.get(userId);
        if (user == null) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new ValidationException("Пользователь с ID " + userId + " не найден");
        }
        log.info("Пользователь с ID {} найден: {}", userId, user.getName());
        return user;
    }

    @GetMapping
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private long getNextId() {
        return nextId++;
    }
}