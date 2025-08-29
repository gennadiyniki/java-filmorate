package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Запрос на создание фильма: {}", film);

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка создания фильма без названия {}", film);
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Попытка создания фильма с превышением допустимой длины: {}", film.getDescription());
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.warn("Дата релиза не указана");
            throw new ValidationException("Дата релиза обязательна");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Попытка создания фильма с некорректной датой: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.warn("Попытка создания фильма c некорректной продолжительностью: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        return nextId++;
    }


    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновление фильма: {}", film);

        if (film.getId() == 0) {
            log.warn("Попытка обновления фильма без ID");
            throw new ValidationException("ID фильма обязателен для обновления");
        }

        Film savedFilm = films.get(film.getId());
        if (savedFilm == null) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new ValidationException("Фильм с ID " + film.getId() + " не найден");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка при обновлении создать пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание: {} символов", film.getDescription().length());
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.warn("Дата релиза не указана при обновлении");
            throw new ValidationException("Дата релиза обязательна");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        savedFilm.setName(film.getName());
        savedFilm.setDescription(film.getDescription());
        savedFilm.setReleaseDate(film.getReleaseDate());
        savedFilm.setDuration(film.getDuration());
        films.put(film.getId(), savedFilm);

        return savedFilm;
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        log.info("Запрос на получение фильма с ID: {}", filmId);
        Film film = films.get(filmId);
        if (film == null) {
            log.warn("Фильм с ID {} не найден", filmId);
            throw new ValidationException("Фильм с ID " + filmId + " не найден");
        }
        log.info("Фильм с ID {} найден: {}", filmId, film.getName());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}