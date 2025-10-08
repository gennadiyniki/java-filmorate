package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotNull
    @Email(message = "Некорректная почта")
    @Column(unique = true, nullable = false)
    String email;

    @NotBlank(message = "Логин не может быть пустым или с пробелами")
    @Column(unique = true, nullable = false)
    String login;

    @NotNull
    @Column(nullable = false)
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @Column(nullable = false)
    LocalDate birthday;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "friends", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "friend_id")
    Set<Long> friends = new HashSet<>();
}