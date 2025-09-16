package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User createUser(User user) {

        for (User savedUser : users.values()) {
            if (savedUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new ValidationException("Этот email уже используется");
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ValidationException("ID пользователя обязателен для обновления");
        }

        if (!users.containsKey(updatedUser.getId())) {
            throw new NotFoundException("Пользователь с ID " + updatedUser.getId() + " не найден");
        }
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private synchronized long getNextId() {
        return nextId++;
    }
}