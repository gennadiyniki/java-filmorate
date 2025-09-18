package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Service
public class FilmServiceImp implements FilmService {
    private final FilmStorage filmStorage;
    private final FilmValidator filmValidator;
    private final UserService userService;

    public FilmServiceImp(FilmStorage filmStorage, FilmValidator filmValidator, UserService userService) {
        this.filmStorage = filmStorage;
        this.filmValidator = filmValidator;
        this.userService = userService;
    }

    @Override
    public Film createFilm(Film film) {
        filmValidator.validateFilm(film);
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        filmValidator.validateFilm(film);
        getFilmById(film.getId());
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        userService.getUserById(userId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк от пользователя не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getFilms();
        films.sort((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()));
        return films.stream().limit(count).toList();
    }
}