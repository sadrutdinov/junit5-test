package com.sai.service;

import com.sai.dto.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void usersEmptyIfNoUsersAdded() {
        UserService userService = new UserService();

        List<User> all = userService.getAll();

        assertTrue(all.isEmpty(), ()-> "User list should be empty");
    }

}