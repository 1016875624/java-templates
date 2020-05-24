package com.hkey.rpc.learn.sample.test;

import com.hkey.rpc.learn.sample.exporter.RpcExporter;
import com.hkey.rpc.learn.sample.importer.RpcImporter;
import com.hkey.rpc.learn.sample.service.EchoService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RpcRealTest {
    // 真正的Rpc调用
    public static void main(String[] args) throws ClassNotFoundException {
        new Thread(() -> {
            try {
                RpcExporter.exporter("localhost", 8088);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        RpcImporter<EchoService> importer = new RpcImporter<>();

        EchoService echo = importer.myimporter(EchoService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(echo.echo("hello."));
    }
}
