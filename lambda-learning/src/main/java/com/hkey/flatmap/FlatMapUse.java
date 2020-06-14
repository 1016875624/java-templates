package com.hkey.flatmap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatMapUse {
    public static void main(String[] args) {
        Stream<List<Integer>> listStream = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4, 5));
        List<Integer> collect = listStream.flatMap(u -> u.stream()).collect(Collectors.toList());
        System.out.println(collect);
    }


}
