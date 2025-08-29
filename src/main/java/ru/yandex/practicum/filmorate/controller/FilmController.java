package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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


    @PutMapping("/{filmId}")
    public Film update(@RequestBody Film film, @PathVariable Long filmId) {
        log.info("Запрос на обновление фильма с ID: {}", filmId);
        Film saveFilm = films.get(filmId);

        if (saveFilm == null) {
            log.warn("Фильм с ID {} не найден", filmId);
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка при обновлении создать пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание: {} символов", film.getDescription().length());
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Некорректная дата релиза {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        saveFilm.setName(film.getName());
        saveFilm.setDescription(film.getDescription());
        saveFilm.setReleaseDate((film.getReleaseDate()));
        saveFilm.setDuration(film.getDuration());
        films.put(filmId, saveFilm);

        return saveFilm;
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        log.info("Запрос на получение фильма с ID: {}", filmId);
        Film film = films.get(filmId);
        if (film == null) {
            log.warn("Фильм с ID {} не найден", filmId);
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        log.info("Фильм с ID {} найден: {}", filmId, film.getName());
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}