package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmValidator filmValidator;
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Запрос на создание фильма: {}", film);

        filmValidator.validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм создан: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновление фильма: {}", film);

        filmValidator.validateIdForUpdate(film.getId());
        filmValidator.validateFilm(film);

        Film savedFilm = films.get(film.getId());
        if (savedFilm == null) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        savedFilm.setName(film.getName());
        savedFilm.setDescription(film.getDescription());
        savedFilm.setReleaseDate(film.getReleaseDate());
        savedFilm.setDuration(film.getDuration());

        log.info("Фильм обновлен: {}", savedFilm);
        return savedFilm;
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

    private long getNextId() {
        return nextId++;
    }
}