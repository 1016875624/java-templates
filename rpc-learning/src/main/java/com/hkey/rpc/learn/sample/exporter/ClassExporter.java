package com.hkey.rpc.learn.sample.exporter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 动态加载类的生产者 这里用做加载本地的class文件 提供给远端调用方
 * @author grayRainbow
 */
public class ClassExporter {
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
                executor.execute(new ClassExporter.ExporterTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }

    /**
     * 生产者任务 实现 提供 class 的byte 数组
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
                String classpath = inputStream.readUTF();

                // 读取文件 然后发送给消费者
                try (FileInputStream fileInputStream = new FileInputStream(new File(classpath))){
                    int len = 0;
                    byte[] buff = new byte[2048];
                    try (ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream())) {
                        // 发送文件给消费者
                        while ((len = fileInputStream.read(buff)) != -1) {
                            outputStream.write(buff, 0, len);
                        }
                        outputStream.flush();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
