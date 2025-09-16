package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor

public class UserServiceImp implements UserService {
    private final UserStorage userStorage;
    private final UserValidator userValidator;

    @Override
    public User createUser(User user) {
        userValidator.validate(user);
        return userStorage.createUser(user);

    }

    @Override
    public User updateUser(User updatedUser) {
        userValidator.validate(updatedUser);
        userStorage.getUserById(updatedUser.getId());
        return userStorage.updateUser(updatedUser);
    }


    @Override
    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public ArrayList<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь не может добавить себя в друзья");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        // Добавляем дружбу в обе стороны
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = getUserById(userId);
        List<User> friendsList = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = getUserById(friendId);
            friendsList.add(friend);
        }
        return friendsList;
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = getUserById(userId1);
        User user2 = getUserById(userId2);

        List<User> commonFriends = new ArrayList<>();

        for (Long friendId : user1.getFriends()) {
            if (user2.getFriends().contains(friendId)) {
                User commonFriend = getUserById(friendId);
                commonFriends.add(commonFriend);
            }
        }

        return commonFriends;
    }
}
