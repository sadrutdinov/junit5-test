package com.sai.service;

import com.sai.dto.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // создается один объект класса UserServiceTest для всех тестов
// @TestInstance(TestInstance.Lifecycle.PER_METHOD) - создается объект для каждого теста, @BeforeAll и @AfterAll должны быть static
public class UserServiceTest {

    @BeforeAll
    void init() {
        System.out.println("BeforeAll: " + this);
        System.out.println();
    }

    private UserService userService;

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
        userService.add(new User());
        userService.add(new User());

        List<User> all = userService.getAll();

        assertEquals(2, all.size());
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