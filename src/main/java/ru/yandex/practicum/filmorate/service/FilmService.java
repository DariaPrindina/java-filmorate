package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final int DEFAULT_POPULAR_COUNT = 10;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        findByIdOrThrow(film.getId());
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        return findByIdOrThrow(id);
    }

    public void addLike(int filmId, int userId) {
        Film film = findByIdOrThrow(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        film.getLikes().add(userId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = findByIdOrThrow(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        film.getLikes().remove(userId);
        log.info("Пользователь id={} удалил лайк с фильма id={}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film findByIdOrThrow(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }
}
