package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User {

    private Long id;
    private String email;
    private String login;
    private String name; //имя для отображения
    private LocalDate birthday; //дата рождения
}

