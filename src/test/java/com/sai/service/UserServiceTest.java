package com.sai.service;

import com.sai.dto.User;
import com.sai.junit.resolver.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;


@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // создается один объект класса UserServiceTest для всех тестов
// @TestInstance(TestInstance.Lifecycle.PER_METHOD) - создается объект для каждого теста, @BeforeAll и @AfterAll должны быть static
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class
})
public class UserServiceTest {

    @BeforeAll
    void init() {
        System.out.println("BeforeAll: " + this);
        System.out.println();
    }

    public UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    private UserService userService;
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("BeforeEach: " + this);
        this.userService = userService;
    }


    @Test
    @Order(1)
    @DisplayName("users will be empty if no  users added")
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
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);


        Map<Integer, User> users = userService.getAllConvertedById();

        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

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


    @Tag("login")
    @Nested
    @DisplayName("test user login functionality")
    class LoginTest {

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

        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }

        @Test
        void loginSuccessIfUserExist() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));

        }


        //        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
//        @ValueSource(strings = {
//                "Ivan", "Pert"
//        })
//        @EnumSource
        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("com.sai.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//        @CsvSource({
//                "Ivan,123",
//                "Petr,111",
//        })

        @DisplayName("login param test")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);

            Optional<User> maybeUser = userService.login(username, password);

            assertThat(maybeUser).isEqualTo(user);

        }


    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );
    }

}