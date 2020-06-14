package com.hkey.min;

import com.hkey.model.User;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class MinUse {

    public static void main(String[] args) {
        getMinValue();
        getMaxValue();
    }

    public static void getMinValue() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(2, "2"), new User(3, "3"));
        Optional<User> min = userStream.min(Comparator.comparing(User::getName));
        System.out.println(min.get());
    }

    public static void getMaxValue() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(2, "2"), new User(3, "3"));
        Optional<User> min = userStream.max(Comparator.comparing(User::getId));
        System.out.println(min.get());
    }
}
