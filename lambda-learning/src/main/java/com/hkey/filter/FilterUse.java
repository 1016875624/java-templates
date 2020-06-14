package com.hkey.filter;

import java.util.stream.Stream;

public class FilterUse {
    public static void main(String[] args) {
        Stream<String> a = Stream.of("a", "b", "c", "d");
        a.filter(u -> u.compareTo("a") > 0).forEach(System.out::println);
    }
}
