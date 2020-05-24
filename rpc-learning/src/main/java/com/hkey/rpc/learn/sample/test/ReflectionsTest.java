package com.hkey.rpc.learn.sample.test;

import com.hkey.rpc.learn.sample.service.EchoService;
import org.reflections.Reflections;

import java.util.Iterator;
import java.util.Set;

public class ReflectionsTest {
    public static void main(String[] args) {
        // MyCommandRunner 与实现了MyService接口的实现类位于同一包下
//        ServiceLoader<EchoService> loader = ServiceLoader.load(EchoService.class);
//        for (EchoService implClass : loader) {
//            System.out.println(implClass.getClass().getSimpleName()); // prints Dog, Cat
//        }
        // 这里定义要扫描的包名
        Reflections reflections = new Reflections("com.hkey.rpc.learn.sample.service");
        // 查找所有的接口 实现类
        Set<Class<? extends EchoService>> classes = reflections.getSubTypesOf(EchoService.class);
        for (Iterator<Class<? extends EchoService>> iterator = classes.iterator(); iterator.hasNext(); ) {
            Class<? extends EchoService> next = iterator.next();
            System.out.println(next.getName());
        }
    }
}
