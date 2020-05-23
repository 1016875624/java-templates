package com.heky.mybatisstudy.user.dao;

import com.heky.mybatisstudy.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
     List<User> findAll();
}
