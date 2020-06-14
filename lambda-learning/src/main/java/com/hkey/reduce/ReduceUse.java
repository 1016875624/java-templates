package com.hkey.reduce;

import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ReduceUse {
    public static void main(String[] args) {
        reduceWithOneParameter();
        reduceWithTwoParameter();
        reduceWithThreeParameter();
    }

    /**
     * 这里没有初始值，它会将第一个元素作为初始值来进行后面的计算
     */
    public static void reduceWithOneParameter() {
        IntStream range = IntStream.range(1, 4);
        OptionalInt reduce = range.reduce((a, b) -> a + b);
        System.out.println(reduce.getAsInt());
    }

    /**
     * 这里定义了初始值，然后再进行计算
     */
    public static void reduceWithTwoParameter() {
        IntStream range = IntStream.range(1, 4);
        int reduce = range.reduce(0, (a, b) -> a + b);
        System.out.println(reduce);
    }

    /**
     * 这里有三个参数，第一个是初始值，第二个是合并两个元素的操作，第三个是合并两个集合的操作
     */
    public static void reduceWithThreeParameter() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3);
        Integer reduce = integerStream.reduce(0, (a, b) -> a + b, (co1, co2) -> co1 + co2);
        System.out.println(reduce);
    }
}
