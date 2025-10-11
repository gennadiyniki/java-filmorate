package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    long id;

    @NotNull
    @Email(message = "Некорректная почта")
    String email;

    @NotBlank(message = "Логин не может быть пустым или с пробелами")
    String login;

    @NotNull
    String name;


    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    @Builder.Default
    Set<Long> friends = new HashSet<>();
}