package com.hkey.rpc.learn.sample.test;

import com.hkey.rpc.learn.sample.exporter.RpcExporter;
import com.hkey.rpc.learn.sample.importer.RpcImporter;
import com.hkey.rpc.learn.sample.service.EchoService;
import com.hkey.rpc.learn.sample.service.EchoServiceImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RpcTest {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                RpcExporter.exporter("localhost", 8088);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        RpcImporter<EchoService> importer = new RpcImporter<>();

        EchoService echo = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 8088));
        System.out.println(echo.echo("hello."));
    }
}
