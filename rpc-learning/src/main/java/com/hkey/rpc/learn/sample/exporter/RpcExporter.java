package com.hkey.rpc.learn.sample.exporter;

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
 * RPC 生产者
 * @author grayRainbow
 */
public class RpcExporter {
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
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                // 调用生产者任务
                executor.execute(new ExporterTask(serverSocket.accept()));
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
                // 获取被调用的全类名
                String serviceName = inputStream.readUTF();
                // 加载类
                Class<?> service = Class.forName(serviceName);

                // 获取调用的方法
                String methodName = inputStream.readUTF();
                // 获取参数类型列表
                Class<?>[] parameter = (Class<?>[]) inputStream.readObject();
                // 获取 传入的参数
                Object[] arg = (Object[]) inputStream.readObject();
                // 利用反射 获取方法
                Method method = service.getMethod(methodName, parameter);
                // 直接实例化一个对象 然后 调用方法
                Object result = method.invoke(service.newInstance(), arg);
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
