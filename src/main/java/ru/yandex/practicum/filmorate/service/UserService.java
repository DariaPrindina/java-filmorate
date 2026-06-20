package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        validate(user);
        setDisplayName(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        findByIdOrThrow(user.getId());
        validate(user);
        setDisplayName(user);
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
        user.getFriends().add((long) friendId);
        friend.getFriends().add((long) userId);
        log.info("Пользователь id={} добавил в друзья id={}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = findByIdOrThrow(userId);
        User friend = findByIdOrThrow(friendId);
        user.getFriends().remove((long) friendId);
        friend.getFriends().remove((long) userId);
        log.info("Пользователь id={} удалил из друзей id={}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = findByIdOrThrow(userId);
        return user.getFriends().stream()
                .map(id -> findByIdOrThrow(id.intValue()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = findByIdOrThrow(userId);
        User other = findByIdOrThrow(otherId);
        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(id -> findByIdOrThrow(id.intValue()))
                .collect(Collectors.toList());
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин содержит пробелы: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }

    private void setDisplayName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User findByIdOrThrow(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}
