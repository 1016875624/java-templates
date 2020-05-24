package com.hkey.rpc.learn.sample.service;

public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String ping) {
        return ping != null ? ping + " --> I am ok." : "I am ok.";
    }
}
