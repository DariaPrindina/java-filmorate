package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получение списка всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        log.info("Получение пользователя id={}", id);
        return userService.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        setDisplayName(user);
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validate(user);
        setDisplayName(user);
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователь id={} добавляет в друзья id={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователь id={} удаляет из друзей id={}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получение списка друзей пользователя id={}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получение общих друзей пользователей id={} и id={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
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
}
