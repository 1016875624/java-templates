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

public class RpcExporter {
    static Executor executor = Executors.newFixedThreadPool(10);

    public static void exporter(String hostName, int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                executor.execute(new ExporterTask(serverSocket.accept()));
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class ExporterTask implements Runnable {

        Socket client = null;

        public ExporterTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream())) {
                String interfaceName = inputStream.readUTF();
                Class<?> service = Class.forName(interfaceName);
                String methodName = inputStream.readUTF();
                Class<?>[] parameter = (Class<?>[]) inputStream.readObject();
                Object[] arg = (Object[]) inputStream.readObject();
                Method method = service.getMethod(methodName, parameter);
                Object result = method.invoke(service.newInstance(), arg);
                try (ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream())) {
                    outputStream.writeObject(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
