package com.hkey.rpc.learn.sample.test;

import com.hkey.rpc.learn.sample.exporter.ClassExporter;
import com.hkey.rpc.learn.sample.importer.ClassImporter;
import com.hkey.rpc.learn.sample.service.EchoService;

import java.io.IOException;

public class ClassTest {
    /**
     * 书上的例子
     * 这个是假的Rpc调用，依赖于 实现类的调用，也就是本身就有这个类了，那么还要远端调用干嘛
     * @param args
     */
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InterruptedException {
        // 注册生产者
        new Thread(() -> {
            try {
                ClassExporter.exporter("localhost", 8088);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 注册消费者，然后实例化消费者
        ClassImporter importer = new ClassImporter("localhost", 8088);

        Class<?> echoServiceImpl = importer.loadClass("EchoServiceImpl");
        EchoService echo = (EchoService) echoServiceImpl.newInstance();
        // 这里使用了 JDK的动态代理，将所有的方法执行都给拦截了，拦截后 进行远端调用
        // 不过这里是个假的实现，发现没有 <b>实现<b/>类直接作为参数进行 传参，那么 就不是 Rpc调用
//        EchoService echo = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 8088));
        System.out.println(echo.echo("hello."));
    }



}
