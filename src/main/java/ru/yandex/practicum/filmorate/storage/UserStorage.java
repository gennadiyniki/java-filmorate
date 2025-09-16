package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

public interface UserStorage {
    public User createUser(User user);

    public User updateUser(User updatedUser);

    public User getUserById(Long userId);

    public ArrayList<User> getUsers();

}
