package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.storage.user.JdbcUserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({JdbcUserRepository.class})
@ContextConfiguration(classes = {FilmorateApplication.class})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JdbcUserRepositoryTest {
    JdbcUserRepository jdbcUserRepository;
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM friendship_status");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
    }

    private User createUser(String email) {
        return User.builder()
                .name("Имя")
                .login("логин")
                .birthday(LocalDate.of(1999, 9, 9))
                .email(email)
                .build();
    }

    @Test
    void createTest() {
        User user = createUser("email@mail.ru");
        User newUser = jdbcUserRepository.create(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", newUser.getId());
        assertThat(newUser).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(newUser).hasFieldOrPropertyWithValue("login", user.getLogin());
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", user.getBirthday());
        assertThat(newUser).hasFieldOrPropertyWithValue("email", user.getEmail());

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "Имя");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "логин");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 9, 9));
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "email@mail.ru");
    }

    @Test
    void getByIdTest() {
        User user = createUser("email@mail.ru");
        User newUser = jdbcUserRepository.create(user);
        User userById = jdbcUserRepository.getById(newUser.getId());

        assertThat(userById).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(userById).hasFieldOrPropertyWithValue("name", "Имя");
        assertThat(userById).hasFieldOrPropertyWithValue("login", "логин");
        assertThat(userById).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 9, 9));
        assertThat(userById).hasFieldOrPropertyWithValue("email", "email@mail.ru");
    }

    @Test
    void getAllTest() {
        User user1 = jdbcUserRepository.create(createUser("user1@mail.ru"));
        User user2 = jdbcUserRepository.create(createUser("user2@mail.ru"));
        Map<Long, User> collection = jdbcUserRepository.getAll();

        assertEquals(2, collection.size());
        assertEquals(user1, collection.get(user1.getId()));
        assertEquals(user2, collection.get(user2.getId()));
    }

    @Test
    void getAllValuesTest() {
        User user1 = jdbcUserRepository.create(createUser("user3@mail.ru"));
        User user2 = jdbcUserRepository.create(createUser("user4@mail.ru"));
        List<User> collection = jdbcUserRepository.getAllValues();

        assertEquals(2, collection.size());
        assertTrue(collection.contains(user1));
        assertTrue(collection.contains(user2));
    }

    @Test
    void updateTest() {
        User user = jdbcUserRepository.create(createUser("email@mail.ru"));
        User userUpdate = User.builder()
                .id(user.getId())
                .login("логин2")
                .name("Имя2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        jdbcUserRepository.update(userUpdate);
        user = jdbcUserRepository.getById(1L);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(user).hasFieldOrPropertyWithValue("name", "Имя2");
        assertThat(user).hasFieldOrPropertyWithValue("login", "логин2");
        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 8, 19));
        assertThat(user).hasFieldOrPropertyWithValue("email", "email2@mail.ru");
    }

    @Test
    void deleteByIdTest() {
        User user1 = jdbcUserRepository.create(createUser("user5@mail.ru"));
        User user2 = jdbcUserRepository.create(createUser("user6@mail.ru"));
        jdbcUserRepository.deleteById(user1.getId());
        Map<Long, User> collection = jdbcUserRepository.getAll();

        assertEquals(1, collection.size());
        assertEquals(collection.get(user2.getId()), user2);
        assertNull(collection.get(user1.getId()));
    }

    @Test
    void findByEmailTest() {
        User user1 = jdbcUserRepository.create(createUser("email@mail.ru"));
        User result = jdbcUserRepository.findByEmail("email@mail.ru");

        assertEquals(result, user1);
    }

    @Test
    void addFriendTest() {
        User user1 = jdbcUserRepository.create(createUser("user7@mail.ru"));
        User user2 = User.builder()
                .login("логин2")
                .name("Имя2")
                .email("user8@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        jdbcUserRepository.create(user2);
        jdbcUserRepository.addFriend(user1.getId(), user2.getId());

        Set<Long> friends = jdbcUserRepository.getFriendIdsFromDB(user1.getId());

        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2.getId()));
    }

    @Test
    void removeFriendTest() {
        User user1 = jdbcUserRepository.create(createUser("user9@mail.ru"));
        User user2 = User.builder()
                .login("логин2")
                .name("Имя2")
                .email("user10@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        jdbcUserRepository.create(user2);
        User user3 = User.builder()
                .login("логин3")
                .name("Имя3")
                .email("user11@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        jdbcUserRepository.create(user3);

        jdbcUserRepository.addFriend(user1.getId(), user2.getId());
        jdbcUserRepository.addFriend(user1.getId(), user3.getId());

        jdbcUserRepository.removeFriend(user1.getId(), user2.getId());

        Set<Long> friends = jdbcUserRepository.getFriendIdsFromDB(user1.getId());

        assertEquals(1, friends.size());
        assertTrue(friends.contains(user3.getId()));
    }

    @Test
    void getFriendIdsFromDB() {
        User user1 = jdbcUserRepository.create(createUser("user12@mail.ru"));
        User user2 = User.builder()
                .login("логин2")
                .name("Имя2")
                .email("user13@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        jdbcUserRepository.create(user2);
        User user3 = User.builder()
                .login("логин3")
                .name("Имя3")
                .email("user14@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        jdbcUserRepository.create(user3);

        jdbcUserRepository.addFriend(user1.getId(), user2.getId());
        jdbcUserRepository.addFriend(user1.getId(), user3.getId());

        Set<Long> friends = jdbcUserRepository.getFriendIdsFromDB(user1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2.getId()));
        assertTrue(friends.contains(user3.getId()));
    }

    @Test
    void getMutualFriends() {
        User user1 = jdbcUserRepository.create(createUser("user15@mail.ru"));
        User user2 = User.builder()
                .login("логин2")
                .name("Имя2")
                .email("user16@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        jdbcUserRepository.create(user2);
        User user3 = User.builder()
                .login("логин3")
                .name("Имя3")
                .email("user17@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        jdbcUserRepository.create(user3);

        jdbcUserRepository.addFriend(user1.getId(), user2.getId());
        jdbcUserRepository.addFriend(user2.getId(), user1.getId());
        jdbcUserRepository.addFriend(user3.getId(), user2.getId());

        List<User> friends = jdbcUserRepository.getMutualFriends(user1.getId(), user3.getId());

        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2));

        friends = jdbcUserRepository.getMutualFriends(user2.getId(), user3.getId());

        assertEquals(0, friends.size());
    }

    @Test
    void getAllFriends() {
        User user1 = jdbcUserRepository.create(createUser("user18@mail.ru"));
        User user2 = User.builder()
                .login("логин2")
                .name("Имя2")
                .email("user19@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        jdbcUserRepository.create(user2);
        User user3 = User.builder()
                .login("логин3")
                .name("Имя3")
                .email("user20@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        jdbcUserRepository.create(user3);

        jdbcUserRepository.addFriend(user1.getId(), user2.getId());
        jdbcUserRepository.addFriend(user1.getId(), user3.getId());

        Set<Long> friends = jdbcUserRepository.getFriendIdsFromDB(user1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2.getId()));
        assertTrue(friends.contains(user3.getId()));
    }
}