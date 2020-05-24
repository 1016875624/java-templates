package com.hkey.rpc.learn.sample.exporter;

import com.hkey.rpc.learn.sample.reflect.util.ReflectUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 我实现的 RPC 生产者
 * 改进了书本的错误例子 生产者 维护了 <b>接口</b>和<b>实现类</b>
 * 消费者只需要维护 <b>接口</b>
 * @author grayRainbow
 */
public class MyRpcExporter {
    /**
     * 创建线程池 用于 并发 使用
     * 生产者里面的所有类都可以进行 RPC调用
     */
    static Executor executor = Executors.newFixedThreadPool(10);

    /**
     * 注册生产者
     * @param hostName IP地址 可以为localhost
     * @param port 端口
     * @throws IOException
     */
    public static void exporter(String hostName, int port) throws IOException {
        // 注册生产者
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                // 调用生产者任务
                executor.execute(new MyRpcExporter.ExporterTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }

    /**
     * 生产者任务 实现
     */
    private static class ExporterTask implements Runnable {

        Socket client = null;

        /**
         * 接受一个socket 客户端
         * @param client
         */
        public ExporterTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream())) {
                // 获取被调用的接口名
                String interfaceName = inputStream.readUTF();
                // 加载类
                Class<?> service = Class.forName(interfaceName);

                // 获取子实现类
                Class subImpletementClass = ReflectUtils.getSubImpletementClass(service);

                // 获取调用的方法
                String methodName = inputStream.readUTF();
                // 获取参数类型列表
                Class<?>[] parameter = (Class<?>[]) inputStream.readObject();
                // 获取 传入的参数
                Object[] arg = (Object[]) inputStream.readObject();
                // 利用反射 获取方法
                Method method = subImpletementClass.getMethod(methodName, parameter);
                // 直接实例化一个对象 然后 调用方法
                Object result = method.invoke(subImpletementClass.newInstance(), arg);
                // 这一步进行  返回 方法执行 结果
                try (ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream())) {
                    outputStream.writeObject(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
