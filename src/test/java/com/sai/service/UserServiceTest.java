package com.sai.service;

import com.sai.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;


@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // создается один объект класса UserServiceTest для всех тестов
// @TestInstance(TestInstance.Lifecycle.PER_METHOD) - создается объект для каждого теста, @BeforeAll и @AfterAll должны быть static
public class UserServiceTest {

    @BeforeAll
    void init() {
        System.out.println("BeforeAll: " + this);
        System.out.println();
    }

    private UserService userService;
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "petr", "111");

    @BeforeEach
    void prepare() {
        System.out.println("BeforeEach: " + this);
        userService = new UserService();
    }


    @Test
    void usersEmptyIfNoUsersAdded() {
        System.out.println("Test1: " + this);
        List<User> users = userService.getAll();
        assertThat(users).isEmpty();

        MatcherAssert.assertThat(users, empty());

//        assertTrue(users.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void userSizeIfUsersAdded() {
        System.out.println("Test2: " + this);


        userService.add(IVAN);
        userService.add(PETR);

        List<User> users = userService.getAll();

        assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
    }

    @Test
    @Tag("login")
    void throwExceptionIfUsernameOrPasswordIsNull() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
        );
    }

    @Test
    @Tag("login")
    void loginSuccessIfUserExist() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));

    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);


        Map<Integer, User> users = userService.getAllConvertedById();

        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

    }

    @Test
    @Tag("login")
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
        assertTrue(maybeUser.isEmpty());
    }

    @Test
    @Tag("login")
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());
        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void deleteDateFromDatabase() {
        System.out.println("AfterEach: " + this);
        System.out.println();
    }

    @AfterAll
    void closeConnectionPool() {

        System.out.println("AfterAll: " + this);
    }

}