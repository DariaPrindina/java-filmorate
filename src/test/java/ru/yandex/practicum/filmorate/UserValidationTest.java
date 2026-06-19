package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {
    private Validator validator;
    private UserController controller;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new UserController();
    }

    private User validUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void emptyEmailFails() {
        User user = validUser();
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidEmailFails() {
        User user = validUser();
        user.setEmail("notanemail");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void emptyLoginFails() {
        User user = validUser();
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void loginWithSpaceFails() {
        User user = validUser();
        user.setLogin("login name");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void emptyNameUsesLogin() {
        User user = validUser();
        user.setName("");
        User created = controller.create(user);
        assertEquals(user.getLogin(), created.getName());
    }

    @Test
    void nullNameUsesLogin() {
        User user = validUser();
        user.setName(null);
        User created = controller.create(user);
        assertEquals(user.getLogin(), created.getName());
    }

    @Test
    void futureBirthdayFails() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void todayBirthdayFails() {
        User user = validUser();
        user.setBirthday(LocalDate.now());
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void pastBirthdayOk() {
        User user = validUser();
        user.setBirthday(LocalDate.now().minusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}
