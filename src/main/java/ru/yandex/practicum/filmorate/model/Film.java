package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    long id;

    @NotBlank(message = "Имя не может быть пустым")
    String name;

    @Size(max = 200, message = "Описание не должно больше 200 символов")
    String description;

    @NotNull
    LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительной")
    long duration;

    @Builder.Default
    Set<Long> likes = new HashSet<>();

    @NotNull(message = "У фильма должен быть указан рейтинг MPA")
    RatingMpa mpa;

    @Builder.Default
    List<Genre> genres = new ArrayList<>();
}