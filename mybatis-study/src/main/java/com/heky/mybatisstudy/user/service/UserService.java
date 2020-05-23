package com.heky.mybatisstudy.user.service;

import com.heky.mybatisstudy.user.dao.UserDao;
import com.heky.mybatisstudy.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Transactional
    public List<User> findAll() {
        return userDao.findAll();
    }
}
