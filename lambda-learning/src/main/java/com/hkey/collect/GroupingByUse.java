package com.hkey.collect;

import com.hkey.model.Student;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupingByUse {
    public static void main(String[] args) {
        Stream<Student> studentStream = Stream.of(new Student(1, "学生1", "一年级"), new Student(2, "学生2", "一年级"), new Student(3, "学生3", "三年级"), new Student(4, "学生4", "二年级"));
        Map<String, List<Student>> collect = studentStream.collect(Collectors.groupingBy(Student::getGrade));
        System.out.println(collect);
    }

    /**
     * 通过年级进行分组
     */
    public static void groupByGrade() {
        Stream<Student> studentStream = Stream.of(new Student(1, "学生1", "一年级"), new Student(2, "学生2", "一年级"), new Student(3, "学生3", "三年级"), new Student(4, "学生4", "二年级"));
        Map<String, List<Student>> collect = studentStream.collect(Collectors.groupingBy(Student::getGrade));
        System.out.println(collect);
    }

    public static void groupByGradeTwoParameter() {
        Stream<Student> studentStream = Stream.of(new Student(1, "学生1", "一年级"), new Student(2, "学生2", "一年级"), new Student(3, "学生3", "三年级"), new Student(4, "学生4", "二年级"));
        Map<String, List<Student>> collect = studentStream.collect(Collectors.groupingBy(Student::getGrade));
        System.out.println(collect);
    }
}
