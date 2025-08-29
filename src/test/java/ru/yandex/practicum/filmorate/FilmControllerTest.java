package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    private Film validFilm;

    @BeforeEach
    void setUp() {

        validFilm = new Film();
        validFilm.setName("Test Film");
        validFilm.setDescription("Test Description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
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
        validFilm.setReleaseDate(LocalDate.of(1894, 12, 31));

        assertThrows(ValidationException.class, () -> filmController.createFilm(validFilm));
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
        validFilm.setDuration(1); // минимальная положительная продолжительность

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void nullDescription() {
        validFilm.setDescription(null);

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }

    @Test
    void emptyDescription() {
        validFilm.setDescription(""); // пустое описание допустимо

        assertDoesNotThrow(() -> filmController.createFilm(validFilm));
    }
}
