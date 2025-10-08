package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmServicerTest {
    @Autowired
    private FilmController filmController;

    private Film validFilm;

    @BeforeEach
    void setUp() {
        RatingMpa mpa = RatingMpa.builder()
                .id(1)
                .name("G")
                .build();

        validFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)  // ← ДОБАВЬТЕ ЭТУ СТРОКУ!
                .mpa(mpa)
                .build();
    }

    @Test
    void createValidFilm() {
        Film createdFilm = filmController.createFilm(validFilm);

        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
        assertEquals("Test Film", createdFilm.getName());
        assertEquals(120, createdFilm.getDuration());
    }

    @Test
    void throwExceptionWhenNameIsEmpty() {
        validFilm.setName("");

        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void throwExceptionWhenNameIsNull() {
        validFilm.setName(null);

        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void throwExceptionWhenReleaseDateBefore1895() {
        Film invalidFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(1894, 12, 31))
                .mpa(validFilm.getMpa())
                .duration(120)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(invalidFilm));
    }

    @Test
    void releaseDateEqualsFirstDateCreate() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void description200Chars() {
        String exact200Chars = "A".repeat(200);
        validFilm.setDescription(exact200Chars);

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void throwExceptionWhenDescription201Chars() {
        String tooLongDescription = "A".repeat(201);
        validFilm.setDescription(tooLongDescription);

        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void throwExceptionWhenDurationZero() {
        validFilm.setDuration(0);

        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void throwExceptionWhenDurationNegative() {
        validFilm.setDuration(-1);

        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
    }

    @Test
    void minimumDuration() {
        validFilm.setDuration(1);

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void nullDescription() {
        validFilm.setDescription(null);

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void emptyDescription() {
        validFilm.setDescription("");

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }
}
