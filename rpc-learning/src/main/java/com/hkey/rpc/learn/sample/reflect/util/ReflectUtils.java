package com.hkey.rpc.learn.sample.reflect.util;

import org.reflections.Reflections;

import java.util.Iterator;
import java.util.Set;

public class ReflectUtils {
    /**
     * 根据接口查找实现类
     * 这个依赖了一个 maven 依赖
     * <dependency>
     *     <groupId>org.reflections</groupId>
     *     <artifactId>reflections</artifactId>
     *     <version>0.9.11</version>
     * </dependency>
     * @param interfaceClass
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static<T> Class getSubImpletementClass(Class<T> interfaceClass) throws ClassNotFoundException {
        // 这里定义要扫描的包名
        Reflections reflections = new Reflections(interfaceClass.getPackage().getName());
        // 查找所有的接口 实现类
        Set<Class<? extends T>> classes = reflections.getSubTypesOf(interfaceClass);
        for (Iterator<Class<? extends T>> iterator = classes.iterator(); iterator.hasNext(); ) {
            Class<? extends T> next = iterator.next();
            System.out.println(next.getName());
            return next;
        }
        throw new ClassNotFoundException(interfaceClass.getName()+"not found implements.");
    }
}
