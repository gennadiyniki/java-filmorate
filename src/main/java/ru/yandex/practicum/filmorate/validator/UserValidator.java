package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Slf4j
@Component
public class UserValidator {

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Пустой email");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!email.contains("@")) {
            log.warn("Email без @: {}", email);
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
    }

    public void validateLogin(String login) {
        if (login == null || login.isBlank()) {
            log.warn("Пустой логин");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (login.contains(" ")) {
            log.warn("Логин с пробелами: {}", login);
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }

    public void validateBirthday(LocalDate birthday) {
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}