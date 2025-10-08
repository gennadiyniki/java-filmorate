package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;


import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public User getUserById(Long id) {

        return users.get(id);
    }

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private synchronized long getNextId() {
        return nextId++;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            if (user.getFriends() == null) {
                user.setFriends(new HashSet<>());
            }
            user.getFriends().add(friendId);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        if (user != null && user.getFriends() != null) {
            user.getFriends().remove(friendId);
        }
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        User user = users.get(userId);
        if (user == null || user.getFriends() == null) {
            return new ArrayList<>();
        }

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = users.get(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    @Override
    public List<User> getMutualFriends(Long userId1, Long userId2) {
        List<User> friends1 = getAllFriends(userId1);
        List<User> friends2 = getAllFriends(userId2);

        return friends1.stream()
                .filter(friends2::contains)
                .collect(Collectors.toList());
    }
}