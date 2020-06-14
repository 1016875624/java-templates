package com.hkey.sorted;

import com.hkey.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortedUse {
    public static void main(String[] args) {
        maxById();
    }

    /**
     * 通过id的大小进行排序
     */
    public static void sortedById() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        List<User> collect = userStream.sorted(Comparator.comparingInt(User::getId)).collect(Collectors.toList());
        System.out.println(collect);
    }

    /**
     * 自定义的方式获取最大值和最小值
     */
    public static void maxById() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        Optional<User> max = userStream.max((u1, u2) -> u2.getId() - u1.getId());
        System.out.println(max.get());
    }
}
