package com.hkey.rpc.learn.sample.importer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MyRpcImporter<S> {

    /**
     * 自己改善的 RPC 方法，真正的不需要实现类的接口，只需要传入 接口即可
     * 不过这个还是 有问题的，比如有多个实现类的时候，并没有指明用哪个，所以后期还是可以改善一下
     * @param interfaces 接口的class
     * @param addr sock地址
     * @return
     * @throws ClassNotFoundException
     */
    public S importer(final Class<?> interfaces, final InetSocketAddress addr) throws ClassNotFoundException {

        return (S) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class<?>[]{interfaces}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket();
                socket.connect(addr);
                try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    // 作为消费者 只维护了接口 因此只能传接口名称过去 让那边加载类，然后再 寻找实现类
                    outputStream.writeUTF(interfaces.getName());
                    // 传入 要调用的方法
                    outputStream.writeUTF(method.getName());
                    // 传入 参数类型
                    outputStream.writeObject(method.getParameterTypes());
                    // 传入 入参
                    outputStream.writeObject(args);
                    // 获取结果
                    try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                        return inputStream.readObject();
                    }
                }
            }
        });
    }


}
