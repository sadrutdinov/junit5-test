package com.sai.service;

import com.sai.dao.UserDAO;
import com.sai.dto.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserService {

    private final List<User> users = new ArrayList<>();

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean delete(Integer userId) {
//        var userIdLocal = 25;
        return userDAO.delete(userId);
    }


    public List<User> getAll() {
        return users;
    }

    public void add(User... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {

        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }

        return users.stream()
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
