package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")

public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;


    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Пустой email");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Email без @: {}", user.getEmail());
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Пустой логин");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Логин с пробелами: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
        for (User saveUser : users.values()) {
            if (saveUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                log.warn("Email уже используется: {}", user.getEmail());
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
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

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser) { // Переименовал параметр
        log.info("Запрос на обновление пользователя с ID: {}", userId);

        if (!users.containsKey(userId)) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User saveUser = users.get(userId);

        if (updatedUser.getEmail() != null) {
            if (updatedUser.getEmail().isBlank()) {
                log.warn("Пустой email");
                throw new ValidationException("Электронная почта не может быть пустой");
            }
            if (!updatedUser.getEmail().contains("@")) {
                log.warn("Email без @: {}", updatedUser.getEmail());
                throw new ValidationException("Электронная почта должна содержать символ @");
            }

            if (!updatedUser.getEmail().equals(saveUser.getEmail())) {
                for (User userInMap : users.values()) {
                    if (userInMap.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
                        log.warn("Email уже используется: {}", updatedUser.getEmail());
                        throw new DuplicatedDataException("Этот имейл уже используется");
                    }
                }
            }
            saveUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getLogin() != null) {
            if (updatedUser.getLogin().isBlank()) {
                log.warn("Пустой логин");
                throw new ValidationException("Логин не может быть пустым");
            }
            if (updatedUser.getLogin().contains(" ")) {
                log.warn("Логин с пробелами: {}", updatedUser.getLogin());
                throw new ValidationException("Логин не может содержать пробелы");
            }
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
            if (updatedUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Дата рождения в будущем: {}", updatedUser.getBirthday());
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            saveUser.setBirthday(updatedUser.getBirthday());
        }

        log.info("Пользователь с ID {} успешно обновлен", userId);
        return saveUser;
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с ID: {}", userId);
        User user = users.get(userId);
        if (user == null) {
            log.warn("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        log.info("Пользователь с ID {} найден: {}", userId, user.getName());
        return user;
    }

    private long getNextId() {
        return nextId++;
    }

    @GetMapping
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}

