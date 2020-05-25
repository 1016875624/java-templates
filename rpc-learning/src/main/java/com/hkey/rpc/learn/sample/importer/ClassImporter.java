package com.hkey.rpc.learn.sample.importer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 动态加载类的方法 这里用于动态加载class 消费者 获取生产者定义的class
 * 缺点 需要知道名字 后期可以改进 这里只是做一个测试罢了
 * @author grayRainbow
 */
public class ClassImporter extends ClassLoader {

    String hostName;
    int port;

    public ClassImporter(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    String path = "D:\\class\\";

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classByteArray;
        try {
            classByteArray = getClassByteArray(name);
        } catch (IOException e) {
            throw new ClassNotFoundException(e.toString());
        }
        // 动态加载只能为null
        return defineClass(null, classByteArray, 0, classByteArray.length);
    }

    public byte[] getClassByteArray(String name) throws IOException, ClassNotFoundException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostName, port));
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            // 发送给 对方文件名称 用于加载类 这里 错了 应该 是生产者去 维护这个路径的 后期可以改一下
            outputStream.writeUTF(path + name + ".class");

            // 因为发送的数据小 为了 避免阻塞 先发送给对方
            outputStream.flush();

            try (ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream())){
                int len;
                byte[] buff = new byte[2048];
                // 读取对方的数据 写入到 byteArray中
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    while ((len = objectInputStream.read(buff)) != -1) {
                        byteArrayOutputStream.write(buff, 0, len);
                    }
                   return byteArrayOutputStream.toByteArray();
                }
            }
        }
    }
}
