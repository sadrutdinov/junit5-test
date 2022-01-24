package com.sai.service;

import com.sai.dao.UserDAO;
import com.sai.dto.User;
import com.sai.junit.TestBase;
import com.sai.junit.extension.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.RepeatedTest.LONG_DISPLAY_NAME;
import static org.mockito.Mockito.*;


@Tag("fast")
@Tag("user")

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // создается один объект класса UserServiceTest для всех тестов
// @TestInstance(TestInstance.Lifecycle.PER_METHOD) - создается объект для каждого теста, @BeforeAll и @AfterAll должны быть static
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class,
//        GlobalExtension.class
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
//        ThrowableExtension.class
})
public class UserServiceTest extends TestBase {

    @BeforeAll
    void init() {
        System.out.println("BeforeAll: " + this);
        System.out.println();
    }

    public UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;

    @Mock(lenient = true)
    private UserDAO userDAO;
    @InjectMocks
    private UserService userService;
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    @BeforeEach
    void prepare() {
        doReturn(true).when(userDAO).delete(IVAN.getId());
//        lenient().when(userDAO.delete(IVAN.getId())).thenReturn(true);

        System.out.println("BeforeEach: " + this);
//        this.userDAO = Mockito.spy(new UserDAO());
//        this.userService = new UserService(userDAO);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDAO).delete(IVAN.getId()); // stub
//        Mockito.doReturn(false).when(userDAO).delete(Mockito.anyInt()); // stub

//        Mockito.when(userDAO.delete(IVAN.getId())).thenReturn(true);
        boolean deleteResult = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));

        verify(userDAO, times(3)).delete(argumentCaptor.capture());


        assertThat(argumentCaptor.getValue()).isEqualTo(IVAN.getId());

//        Mockito.reset(userDAO);

        assertThat(deleteResult).isTrue();
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailable() {
        doThrow(RuntimeException.class).when(userDAO).delete(IVAN.getId());
        assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
    }


    @Test
    @Order(1)
    @DisplayName("users will be empty if no  users added")
    void usersEmptyIfNoUsersAdded() throws IOException {
        if (true) {
            throw new RuntimeException();
        }
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
        @Disabled("flaky, need to see")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");
            assertTrue(maybeUser.isEmpty());
        }

//        @Test
        @RepeatedTest(value = 5, name = LONG_DISPLAY_NAME)
        void loginFailIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());
            assertTrue(maybeUser.isEmpty());
        }

        @Test
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            Optional<User> user = assertTimeoutPreemptively(Duration.ofMillis(200L), () ->{
                System.out.println(Thread.currentThread().getName());
                return userService.login("dummy", IVAN.getPassword());
            });

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