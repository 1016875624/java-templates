package com.hkey.rpc.learn.sample.importer;

import com.hkey.rpc.learn.sample.reflect.util.ReflectUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcImporter<S> {
    /**
     * 书上的例子 居然要 <b>实现类</b>  的class 作为参数进行传参，这已经是本地调用了
     * 真正的RPC 是 维护一套接口即可 实现类 交给 生产者去实现
     * @param serviceClass 实现类的class
     * @param addr socket地址
     * @return jdk动态代理类
     */
    public S importer(final Class<?> serviceClass, final InetSocketAddress addr) {
        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass.getInterfaces()[0]}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket();
                socket.connect(addr);
                try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    outputStream.writeUTF(serviceClass.getName());
                    outputStream.writeUTF(method.getName());
                    outputStream.writeObject(method.getParameterTypes());
                    outputStream.writeObject(args);
                    try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                        return inputStream.readObject();
                    }
                }
            }
        });
    }


    /**
     * 自己改善的 RPC 方法，真正的不需要实现类的接口，只需要传入 接口即可
     * 不过这个还是 有问题的，比如有多个实现类的时候，并没有指明用哪个，所以后期还是可以改善一下
     * @param interfaces 接口的class
     * @param addr sock地址
     * @return
     * @throws ClassNotFoundException
     */
    public S myimporter(final Class<?> interfaces, final InetSocketAddress addr) throws ClassNotFoundException {

        Class subImpletementClass = ReflectUtils.getSubImpletementClass(interfaces);
        return (S) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class<?>[]{interfaces}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket();
                socket.connect(addr);
                try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
                    outputStream.writeUTF(subImpletementClass.getName());
                    outputStream.writeUTF(method.getName());
                    outputStream.writeObject(method.getParameterTypes());
                    outputStream.writeObject(args);
                    try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
                        return inputStream.readObject();
                    }
                }
            }
        });
    }


}
