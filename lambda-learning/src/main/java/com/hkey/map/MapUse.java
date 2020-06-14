package com.hkey.map;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MapUse {
    public static void main(String[] args) {
        mapToAddNum();
    }

    /**
     * 将所有元素转换为大写
     */
    public static void mapToUpperCase() {
        Stream<String> a = Stream.of("a", "b", "c", "d");
        a.map(u -> u.toUpperCase(Locale.ENGLISH)).forEach(System.out::println);
    }

    /**
     * 将所有的数字都添加17
     */
    public static void mapToAddNum() {
        IntStream integerStream = IntStream.of(1, 2, 3, 4);
        integerStream.map(u->u+17).forEach(System.out::println);
    }
}
