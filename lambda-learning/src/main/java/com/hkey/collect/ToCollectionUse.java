package com.hkey.collect;

import com.hkey.model.User;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToCollectionUse {
    public static void main(String[] args) {
        toCopyOnWriteList();
        toHashSet();
    }

    /**
     * 转换为 hashSet
     */
    public static void toHashSet() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        HashSet<User> collect = userStream.collect(Collectors.toCollection(HashSet::new));
        System.out.println(collect);
    }

    /**
     *  转换为copyOnWriteList
     */
    public static void toCopyOnWriteList() {
        Stream<User> userStream = Stream.of(new User(1, "1"), new User(8, "8"), new User(5, "5"));
        CopyOnWriteArrayList<User> collect = userStream.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        System.out.println(collect);
    }
}
