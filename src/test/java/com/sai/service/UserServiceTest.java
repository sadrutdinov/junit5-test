package com.sai.service;

import com.sai.dto.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        List<User> all = userService.getAll();

        assertTrue(all.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void userSizeIfUsersAdded() {
        System.out.println("Test2: " + this);


        userService.add(IVAN);
        userService.add(PETR);

        List<User> all = userService.getAll();

        assertEquals(2, all.size());
    }

    @Test
    void loginSuccessIfUserExist() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(IVAN, user));

    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
        assertTrue(maybeUser.isEmpty());
    }

    @Test
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