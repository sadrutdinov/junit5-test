package com.sai.dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDaoMock extends UserDAO {

    private Map<Integer, Boolean> answers = new HashMap<>();
//    private Answer1<Integer, Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId, false);
    }
}
