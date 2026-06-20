package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        findByIdOrThrow(user.getId());
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return findByIdOrThrow(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = findByIdOrThrow(userId);
        User friend = findByIdOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь id={} добавил в друзья id={}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = findByIdOrThrow(userId);
        User friend = findByIdOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь id={} удалил из друзей id={}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = findByIdOrThrow(userId);
        return user.getFriends().stream()
                .map(this::findByIdOrThrow)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = findByIdOrThrow(userId);
        User other = findByIdOrThrow(otherId);
        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::findByIdOrThrow)
                .collect(Collectors.toList());
    }

    private User findByIdOrThrow(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
