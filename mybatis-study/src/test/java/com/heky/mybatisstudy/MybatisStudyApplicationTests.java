package com.heky.mybatisstudy;

import com.heky.mybatisstudy.config.datasoutce.DatabaseContextHolder;
import com.heky.mybatisstudy.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@SpringBootTest
class MybatisStudyApplicationTests {

    @Autowired
    UserService userService;

    @Autowired
    AbstractRoutingDataSource routingDataSource;
    @Test
    void contextLoads() {

        // 用第一个数据库进行查询数据
        DatabaseContextHolder.setDbType("slave");
        System.out.println(userService.findAll());

        // 用第二个数据库进行查询数据
        DatabaseContextHolder.setDbType("master");
        System.out.println(userService.findAll());


    }

    @Test
    void test() {
        // 用第一个数据库进行查询数据
        DatabaseContextHolder.setDbType("slave");
        System.out.println(userService.findAll());

        // 用第二个数据库进行查询数据
        DatabaseContextHolder.setDbType("master");
        System.out.println(userService.findAll());
    }

}
