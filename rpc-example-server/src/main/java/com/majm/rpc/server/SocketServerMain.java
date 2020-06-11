package com.majm.rpc.server;

import com.majm.rpc.api.HelloService;
import com.majm.rpc.provider.ServiceProviderImpl;
import com.majm.rpc.server.service.impl.HelloServiceImpl;
import com.majm.rpc.transport.socket.SocketRpcServer;

/**
 * @author majunmin
 * @description
 * @datetime 2020/6/10 4:45 下午
 * @since
 */
public class SocketServerMain {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceProviderImpl registry = new ServiceProviderImpl();
        // 服务注册,  后来可以改成注解注册
        registry.addServiceProvider(helloService, HelloService.class);
        new SocketRpcServer("localhost", 9000).start();
    }
}
